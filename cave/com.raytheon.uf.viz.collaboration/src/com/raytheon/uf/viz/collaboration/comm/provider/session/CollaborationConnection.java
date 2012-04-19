/**
 * This software was developed and / or modified by Raytheon Company,
 * pursuant to Contract DG133W-05-CQ-1067 with the US Government.
 * 
 * U.S. EXPORT CONTROLLED TECHNICAL DATA
 * This software product contains export-restricted data whose
 * export/transfer/disclosure is restricted by U.S. law. Dissemination
 * to non-U.S. persons whether in the United States or abroad requires
 * an export license or other authorization.
 * 
 * Contractor Name:        Raytheon Company
 * Contractor Address:     6825 Pine Street, Suite 340
 *                         Mail Stop B8
 *                         Omaha, NE 68106
 *                         402.291.0100
 * 
 * See the AWIPS II Master Rights File ("Master Rights File.pdf") for
 * further licensing information.
 **/
package com.raytheon.uf.viz.collaboration.comm.provider.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.chatroom.IChatRoomInfo;
import org.eclipse.ecf.presence.chatroom.IChatRoomInvitationListener;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.IRosterListener;
import org.eclipse.ecf.provider.xmpp.identity.XMPPRoomID;

import com.google.common.eventbus.EventBus;
import com.raytheon.uf.common.status.IUFStatusHandler;
import com.raytheon.uf.common.status.UFStatus;
import com.raytheon.uf.common.status.UFStatus.Priority;
import com.raytheon.uf.viz.collaboration.comm.identity.CollaborationException;
import com.raytheon.uf.viz.collaboration.comm.identity.IAccountManager;
import com.raytheon.uf.viz.collaboration.comm.identity.IPresence;
import com.raytheon.uf.viz.collaboration.comm.identity.ISession;
import com.raytheon.uf.viz.collaboration.comm.identity.ISharedDisplaySession;
import com.raytheon.uf.viz.collaboration.comm.identity.IVenueSession;
import com.raytheon.uf.viz.collaboration.comm.identity.event.IEventPublisher;
import com.raytheon.uf.viz.collaboration.comm.identity.event.IRosterChangeEvent;
import com.raytheon.uf.viz.collaboration.comm.identity.event.IRosterEventSubscriber;
import com.raytheon.uf.viz.collaboration.comm.identity.event.IVenueInvitationEvent;
import com.raytheon.uf.viz.collaboration.comm.identity.event.RosterChangeType;
import com.raytheon.uf.viz.collaboration.comm.identity.info.IVenueInfo;
import com.raytheon.uf.viz.collaboration.comm.identity.invite.SharedDisplayVenueInvite;
import com.raytheon.uf.viz.collaboration.comm.identity.invite.VenueInvite;
import com.raytheon.uf.viz.collaboration.comm.identity.roster.IRosterManager;
import com.raytheon.uf.viz.collaboration.comm.identity.user.IQualifiedID;
import com.raytheon.uf.viz.collaboration.comm.provider.Presence;
import com.raytheon.uf.viz.collaboration.comm.provider.Tools;
import com.raytheon.uf.viz.collaboration.comm.provider.event.RosterChangeEvent;
import com.raytheon.uf.viz.collaboration.comm.provider.event.VenueInvitationEvent;
import com.raytheon.uf.viz.collaboration.comm.provider.info.InfoAdapter;
import com.raytheon.uf.viz.collaboration.comm.provider.roster.RosterEntry;
import com.raytheon.uf.viz.collaboration.comm.provider.roster.RosterManager;
import com.raytheon.uf.viz.collaboration.comm.provider.user.IDConverter;
import com.raytheon.uf.viz.collaboration.comm.provider.user.UserId;
import com.raytheon.uf.viz.collaboration.comm.provider.user.VenueId;

/**
 * 
 * <ul>
 * <li>EventBus subscription events.</li>
 * <ul>
 * <li><strong>IVenueInvitationEvent</strong> : This event is posted when the
 * SessionManager receives a venue invitation requesting that the user join some
 * particular collaboration session.</li>
 * <li><strong>IConnectionStatusEvent</strong> : This event is posted when the
 * state of the underlying connection changes, reconnecting, connecting,
 * disconnected, for example.</li>
 * <li><strong>IRosterChangeEvent</strong> : This event is posted when roster
 * changes have occurred.</li>
 * <li><strong>---------------</strong> : ---------------.</li>
 * </ul>
 * </ul>
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * 
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Feb 24, 2012            jkorman     Initial creation
 * Apr 18, 2012            njensen      Major cleanup
 * 
 * </pre>
 * 
 * @author jkorman
 * @version 1.0
 * @see com.raytheon.uf.viz.collaboration.comm.identity.event.IVenueInvitationEvent
 */

public class CollaborationConnection implements IEventPublisher {

    private static final transient IUFStatusHandler statusHandler = UFStatus
            .getHandler(CollaborationConnection.class);

    private static final String PROVIDER = "ecf.xmpp.smack";

    private Map<String, ISession> sessions;

    private String account;

    private String password;

    private UserId user;

    private IPresence userPresence;

    private IChatRoomInvitationListener intInvitationListener;

    private IPresenceContainerAdapter presenceAdapter;

    private Namespace connectionNamespace = null;

    private PeerToPeerChat chatInstance = null;

    private IAccountManager accountManager = null;

    private IRosterManager rosterManager = null;

    private IContainer container = null;

    private EventBus eventBus;

    private IRosterEventSubscriber rosterEventSubscriber = null;

    // Debug -- event viewer ----------------
    private IRosterEventSubscriber rosterEventHandler = null;

    // Debug -- event viewer ----------------

    /**
     * @throws CollaborationException
     * @throws ContainerCreateException
     * 
     */
    public CollaborationConnection(String account, String password)
            throws CollaborationException {
        this(account, password, (IRosterEventSubscriber) null);
    }

    /**
     * 
     * @param account
     *            The account name to connect to.
     * @param password
     *            The password to use for connection.
     * @param initialPresence
     *            The initial presence for the account name.
     * @throws ContainerCreateException
     * 
     */
    public CollaborationConnection(String account, String password,
            IPresence initialPresence) throws Exception {
        this(account, password, (IRosterEventSubscriber) null);
        if (accountManager != null) {
            userPresence = initialPresence;
            accountManager.sendPresence(initialPresence);
        }
    }

    /**
     * 
     * The roster event subscriber must be ready to accept events before this
     * constructor is called.
     * 
     * @param account
     *            The account name to connect to.
     * @param password
     *            The password to use for connection.
     * @param rosterEventSubscriber
     *            A roster event subscriber.
     * @throws CollaborationException
     */
    public CollaborationConnection(String account, String password,
            IRosterEventSubscriber rosterEventSubscriber)
            throws CollaborationException {
        eventBus = new EventBus();
        if (rosterEventSubscriber != null) {
            this.rosterEventSubscriber = rosterEventSubscriber;
            eventBus.register(rosterEventSubscriber);
        }
        // Debug -- event viewer ----------------
        rosterEventHandler = new RosterEventHandler();
        eventBus.register(rosterEventHandler);
        // Debug -- event viewer ----------------

        sessions = new HashMap<String, ISession>();

        try {
            container = ContainerFactory.getDefault().createContainer(PROVIDER);

            if (container != null) {
                container.addListener(new IContainerListener() {

                    @Override
                    public void handleEvent(IContainerEvent event) {

                        System.out.println("ContainerEvent.Type = "
                                + event.getClass().getName());

                    }
                });
            }

        } catch (ContainerCreateException cce) {
            closeInternals();
            throw new CollaborationException(String.format(
                    "Could not create container for provider [%s]", PROVIDER));
        }
        this.account = account;
        this.password = password;
        try {
            connectToContainer();
        } catch (ContainerConnectException e) {
            closeInternals();
            throw new CollaborationException(
                    "Login failed.  Invalid username or password", e);
        }
        ID id = container.getConnectedID();
        if (id != null) {
            String name = Tools.parseName(id.getName());
            String host = Tools.parseHost(id.getName());
            String resource = Tools.parseResource(id.getName());
            user = new UserId(name, host, resource);
        }

        setupAccountManager();

        setupInternalConnectionListeners();
        setupInternalVenueInvitationListener();
        setupP2PComm(presenceAdapter);
        getPeerToPeerSession();

    }

    /**
     * @return
     * @see com.raytheon.uf.viz.collaboration.comm.identity.roster.IRoster#getUser()
     */
    public UserId getUser() {
        return user;
    }

    /**
     * 
     * @return
     */
    public IPresence getPresence() {
        return userPresence;
    }

    /**
     * 
     * @return
     */
    public void setPresence(IPresence presence) {
        userPresence = presence;
    }

    /**
     * @throws CollaborationException
     * @throws ContainerConnectException
     * 
     */
    private void connectToContainer() throws CollaborationException,
            ContainerConnectException {
        if (container.getConnectedID() == null) {
            connectionNamespace = container.getConnectNamespace();

            // Now connect
            ID targetID = createID(account);
            container.connect(targetID, ConnectContextFactory
                    .createPasswordConnectContext(password));

            presenceAdapter = Tools.getPresenceContainerAdapter(container,
                    IPresenceContainerAdapter.class);
        }
    }

    /**
     * 
     */
    private void setupAccountManager() {
        if (accountManager == null) {
            if (isConnected() && (presenceAdapter != null)) {
                accountManager = new AccountManager(presenceAdapter, this);
            }
        }
    }

    /**
     * Return the account string that was used to create this manager.
     * 
     * @return The account string.
     */
    public String getAccount() {
        return account;
    }

    /**
     * Get the account manager for this connection.
     * 
     * @return The account manager for this connection.
     */
    public IAccountManager getAccountManager() {
        if (accountManager == null) {
            setupAccountManager();
        }
        return accountManager;
    }

    /**
     * 
     */
    private void setupRosterManager() {
        rosterManager = new RosterManager(this);
    }

    /**
     * 
     * @return
     */
    public IPresenceContainerAdapter getPresenceContainerAdapter() {
        return presenceAdapter;
    }

    /**
     * 
     * @return
     */
    public IRosterManager getRosterManager() {
        if (rosterManager == null) {
            setupRosterManager();
        }
        return rosterManager;
    }

    /**
     * Is this SessionManager currently connected?
     * 
     * @return Is this SessionManager currently connected?
     */
    public boolean isConnected() {
        return ((container != null) && (container.getConnectedID() != null));
    }

    private void closeInternals() {
        if (rosterEventSubscriber != null) {
            eventBus.unregister(rosterEventSubscriber);
        }
        if (container != null) {

            chatInstance = null;
            // Get rid of the account and roster managers
            container.dispose();
            container = null;
        }
    }

    /**
     *  
     */
    public void closeManager() {
        if (container != null) {
            // Close any created sessions.
            for (ISession session : sessions.values()) {
                if ((chatInstance != null) && chatInstance.equals(session)) {
                    chatInstance.close();
                    chatInstance = null;
                } else {
                    session.close();
                }
            }
            chatInstance = null;
        }
        closeInternals();
    }

    /**
     * Get the PeerToPeerChat session instance.
     * 
     * @return
     */
    public ISession getPeerToPeerSession() throws CollaborationException {
        if (chatInstance == null) {
            chatInstance = new PeerToPeerChat(container, eventBus, this);
            sessions.put(chatInstance.getSessionId(), chatInstance);
        }
        return chatInstance;
    }

    public ISharedDisplaySession joinCollaborationVenue(
            IVenueInvitationEvent invitation) throws CollaborationException {
        SharedDisplaySession session = null;
        try {
            String venueName = invitation.getRoomId().getName();
            String sessionId = invitation.getInvite().getSessionId();
            session = new SharedDisplaySession(container, eventBus, this,
                    sessionId);
            if (session != null) {
                session.joinVenue(venueName);

                String name = Tools.parseName(account);
                String host = Tools.parseHost(account);
                UserId me = new UserId(name, host);
                session.setUserId(me);
                if (invitation instanceof SharedDisplayVenueInvite) {
                    SharedDisplayVenueInvite invite = (SharedDisplayVenueInvite) invitation;
                    session.setCurrentDataProvider(invite.getDataProvider());
                    session.setCurrentSessionLeader(invite.getSessionLeader());
                }
                sessions.put(session.getSessionId(), session);
            }
        } catch (Exception e) {
            throw new CollaborationException("Error joining venue "
                    + invitation.getRoomId().getName(), e);
        }
        return session;
    }

    /**
     * 
     * @param venueName
     * @return
     * @throws CollaborationException
     */
    public ISharedDisplaySession createCollaborationVenue(String venueName,
            String subject) throws CollaborationException {
        SharedDisplaySession session = null;
        try {
            session = new SharedDisplaySession(container, eventBus, this);

            session.createVenue(venueName, subject);
            String name = Tools.parseName(account);
            String host = Tools.parseHost(account);

            UserId me = new UserId(name, host);

            session.setCurrentSessionLeader(me);
            session.setCurrentDataProvider(me);
            session.setUserId(me);

            sessions.put(session.getSessionId(), session);
            return session;
        } catch (Exception e) {
            throw new CollaborationException(
                    "Error creating collaboration venue " + venueName, e);
        }
    }

    /**
     * 
     * @param venueName
     * @return
     * @throws CollaborationException
     */
    public IVenueSession joinTextOnlyVenue(String venueName)
            throws CollaborationException {
        VenueSession session = null;
        try {
            session = new VenueSession(container, eventBus, this);
            if (session != null) {
                session.joinVenue(venueName);
                sessions.put(session.getSessionId(), session);
            }
        } catch (Exception e) {
            throw new CollaborationException(
                    "Error joining venue " + venueName, e);
        }
        return session;
    }

    /**
     * 
     * @param venueName
     * @return
     * @throws CollaborationException
     */
    public IVenueSession createTextOnlyVenue(String venueName, String subject)
            throws CollaborationException {
        VenueSession session = null;
        try {
            session = new VenueSession(container, eventBus, this);
            if (session != null) {
                session.createVenue(venueName, subject);
                sessions.put(session.getSessionId(), session);
            }
        } catch (Exception e) {
            throw new CollaborationException("Error creating venue "
                    + venueName, e);
        }
        return session;
    }

    /**
     * 
     * @param session
     */
    protected void removeSession(ISession session) {
        sessions.remove(session);
    }

    /**
     * 
     * @return
     */
    public Collection<IVenueInfo> getVenueInfo() {
        // Check to see if the container has been connected.
        Collection<IVenueInfo> info = new ArrayList<IVenueInfo>();
        if (isConnected()) {
            IPresenceContainerAdapter presenceAdapter = Tools
                    .getPresenceContainerAdapter(container,
                            IPresenceContainerAdapter.class);
            IChatRoomManager venueManager = presenceAdapter
                    .getChatRoomManager();
            if (venueManager != null) {
                IChatRoomInfo[] roomInfo = venueManager.getChatRoomInfos();
                for (IChatRoomInfo rInfo : roomInfo) {
                    IVenueInfo vi = InfoAdapter.createVenueInfo(rInfo);
                    if (vi != null) {
                        info.add(vi);
                    }
                }
            }
        }

        return info;
    }

    // ***************************
    // Connection listener
    // ***************************

    /**
     * 
     */
    private void setupInternalConnectionListeners() {

        presenceAdapter.getRosterManager().addPresenceListener(
                new IPresenceListener() {

                    @Override
                    public void handlePresence(ID fromId,
                            org.eclipse.ecf.presence.IPresence presence) {

                        IPresence p = Presence.convertPresence(presence);

                        String name = Tools.parseName(fromId.getName());
                        String host = Tools.parseHost(fromId.getName());
                        String resource = Tools.parseResource(fromId.getName());

                        UserId id = new UserId(name, host, resource);

                        if (rosterManager != null) {
                            ((RosterManager) rosterManager).updateEntry(id, p);
                        }
                    }
                });

        presenceAdapter.getRosterManager().addRosterListener(
                new IRosterListener() {

                    @Override
                    public void handleRosterEntryAdd(IRosterEntry entry) {
                        com.raytheon.uf.viz.collaboration.comm.identity.roster.IRosterEntry re = RosterEntry
                                .convertEntry(entry);
                        IRosterChangeEvent event = new RosterChangeEvent(
                                RosterChangeType.ADD, re);
                        eventBus.post(event);
                    }

                    @Override
                    public void handleRosterUpdate(IRoster roster,
                            IRosterItem item) {
                        if (item instanceof IRosterEntry) {
                            com.raytheon.uf.viz.collaboration.comm.identity.roster.IRosterEntry re = RosterEntry
                                    .convertEntry((IRosterEntry) item);
                            IRosterChangeEvent event = new RosterChangeEvent(
                                    RosterChangeType.MODIFY, re);
                            eventBus.post(event);
                        } else if (item instanceof IRosterGroup) {
                            IRosterGroup rg = (IRosterGroup) item;
                            System.out.println("Roster update RosterGroup "
                                    + rg.getName());
                            // System.out.println("         entries "
                            // + rg.getEntries());
                            // System.out.println("         name " +
                            // rg.getName());
                        } else if (item instanceof IRoster) {
                            IRoster r = (IRoster) item;
                            System.out.println("Roster update Roster "
                                    + r.getName());
                        }
                    }

                    @Override
                    public void handleRosterEntryRemove(IRosterEntry entry) {
                        com.raytheon.uf.viz.collaboration.comm.identity.roster.IRosterEntry re = RosterEntry
                                .convertEntry((IRosterEntry) entry);
                        IRosterChangeEvent event = new RosterChangeEvent(
                                RosterChangeType.MODIFY, re);
                        eventBus.post(event);
                    }
                });
    }

    // ***************************
    // Peer to Peer communications
    // ***************************

    /**
     * 
     */
    protected ISession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    private void setupP2PComm(IPresenceContainerAdapter presenceAdapter) {
        if (isConnected() && (presenceAdapter != null)) {
            PeerToPeerCommHelper helper = new PeerToPeerCommHelper(this);
            presenceAdapter.getChatManager().addMessageListener(helper);
        }
    }

    // ***************************
    // Venue invitation listener management
    // ***************************

    /**
     * 
     */
    private void setupInternalVenueInvitationListener() {
        if (isConnected() && (presenceAdapter != null)) {
            IChatRoomManager venueManager = presenceAdapter
                    .getChatRoomManager();
            if (venueManager != null) {
                intInvitationListener = new IChatRoomInvitationListener() {
                    @Override
                    public void handleInvitationReceived(ID roomID, ID from,
                            String subject, String body) {

                        IQualifiedID venueId = null;
                        if (roomID instanceof XMPPRoomID) {
                            XMPPRoomID room = (XMPPRoomID) roomID;
                            venueId = new VenueId();
                            venueId.setName(room.getLongName());

                        }
                        if (venueId != null) {
                            IQualifiedID id = IDConverter.convertFrom(from);

                            UserId invitor = new UserId(id.getName(),
                                    id.getHost(), id.getResource());

                            VenueInvite received;
                            try {
                                received = (VenueInvite) Tools
                                        .unMarshallData(body);

                                if (subject == null) {
                                    subject = received.getSubject();
                                    if (subject == null) {
                                        subject = presenceAdapter
                                                .getChatRoomManager()
                                                .getChatRoomInfo(
                                                        roomID.getName())
                                                .getSubject();
                                    }
                                }

                                IVenueInvitationEvent invite = new VenueInvitationEvent(
                                        venueId, invitor, subject, received);
                                eventBus.post(invite);
                            } catch (CollaborationException e) {
                                statusHandler
                                        .handle(Priority.PROBLEM,
                                                "Error handling received invite message",
                                                e);
                            }

                        }
                    }
                };
                venueManager.addInvitationListener(intInvitationListener);
            }
        }
    }

    /**
     * Register an event handler with this
     * 
     * @see com.raytheon.uf.viz.collaboration.comm.identity.event.IEventPublisher#registerEventHandler(java.lang.Object)
     */
    @Override
    public void registerEventHandler(Object handler) {
        eventBus.register(handler);
    }

    /**
     * @see com.raytheon.uf.viz.collaboration.comm.identity.event.IEventPublisher#unRegisterEventHandler(java.lang.Object)
     */
    @Override
    public void unRegisterEventHandler(Object handler) {
        eventBus.unregister(handler);
    }

    /**
     * @see com.raytheon.uf.viz.collaboration.comm.identity.event.IEventPublisher#getEventPublisher()
     */
    @Override
    public EventBus getEventPublisher() {
        return eventBus;
    }

    /**
     * 
     * @param name
     * @return
     */
    public ID createID(String name) throws CollaborationException {
        ID id = null;
        try {
            if (connectionNamespace != null) {
                id = IDFactory.getDefault().createID(connectionNamespace, name);
            }
        } catch (IDCreateException idce) {
            throw new CollaborationException("Could not create id");
        }
        return id;
    }

}
