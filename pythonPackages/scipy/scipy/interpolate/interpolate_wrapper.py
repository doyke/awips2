""" helper_funcs.py.
    scavenged from enthought,interpolate
"""
<<<<<<< HEAD

import numpy as np
import sys
import _interpolate # C extension.  Does all the real work.

def atleast_1d_and_contiguous(ary, dtype = np.float64):
    return np.atleast_1d( np.ascontiguousarray(ary, dtype) )

def nearest(x, y, new_x):
    """ Rounds each new_x[i] to the closest value in x
        and returns corresponding y.
    """
    shifted_x = np.concatenate(( np.array([x[0]-1]) , x[0:-1] ))

    midpoints_of_x = atleast_1d_and_contiguous( .5*(x + shifted_x) )
=======
from __future__ import division, print_function, absolute_import

import numpy as np
from . import _interpolate  # C extension.  Does all the real work.


def atleast_1d_and_contiguous(ary, dtype=np.float64):
    return np.atleast_1d(np.ascontiguousarray(ary, dtype))


def nearest(x, y, new_x):
    """
    Rounds each new x to nearest input x and returns corresponding input y.

    Parameters
    ----------
    x : array_like
        Independent values.
    y : array_like
        Dependent values.
    new_x : array_like
        The x values to return the interpolate y values.

    Returns
    -------
    nearest : ndarray
        Rounds each `new_x` to nearest `x` and returns the corresponding `y`.

    """
    shifted_x = np.concatenate((np.array([x[0]-1]), x[0:-1]))

    midpoints_of_x = atleast_1d_and_contiguous(.5*(x + shifted_x))
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    new_x = atleast_1d_and_contiguous(new_x)

    TINY = 1e-10
    indices = np.searchsorted(midpoints_of_x, new_x+TINY)-1
<<<<<<< HEAD
    indices = np.atleast_1d(np.clip(indices, 0, np.Inf).astype(np.int))
=======
    indices = np.atleast_1d(np.clip(indices, 0, np.Inf).astype(int))
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    new_y = np.take(y, indices, axis=-1)

    return new_y


<<<<<<< HEAD

def linear(x, y, new_x):
    """ Linearly interpolates values in new_x based on the values in x and y

        Parameters
        ----------
        x
            1-D array
        y
            1-D or 2-D array
        new_x
            1-D array
=======
def linear(x, y, new_x):
    """
    Linearly interpolates values in new_x based on the values in x and y

    Parameters
    ----------
    x : array_like
        Independent values
    y : array_like
        Dependent values
    new_x : array_like
        The x values to return the interpolated y values.

>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    """
    x = atleast_1d_and_contiguous(x, np.float64)
    y = atleast_1d_and_contiguous(y, np.float64)
    new_x = atleast_1d_and_contiguous(new_x, np.float64)

<<<<<<< HEAD
    assert len(y.shape) < 3, "function only works with 1D or 2D arrays"
    if len(y.shape) == 2:
        new_y = np.zeros((y.shape[0], len(new_x)), np.float64)
        for i in range(len(new_y)): # for each row
=======
    if y.ndim > 2:
        raise ValueError("`linear` only works with 1-D or 2-D arrays.")
    if len(y.shape) == 2:
        new_y = np.zeros((y.shape[0], len(new_x)), np.float64)
        for i in range(len(new_y)):  # for each row
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
            _interpolate.linear_dddd(x, y[i], new_x, new_y[i])
    else:
        new_y = np.zeros(len(new_x), np.float64)
        _interpolate.linear_dddd(x, y, new_x, new_y)

    return new_y

<<<<<<< HEAD
def logarithmic(x, y, new_x):
    """ Linearly interpolates values in new_x based in the log space of y.

        Parameters
        ----------
        x
            1-D array
        y
            1-D or 2-D array
        new_x
            1-D array
=======

def logarithmic(x, y, new_x):
    """
    Linearly interpolates values in new_x based in the log space of y.

    Parameters
    ----------
    x : array_like
        Independent values.
    y : array_like
        Dependent values.
    new_x : array_like
        The x values to return interpolated y values at.

>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    """
    x = atleast_1d_and_contiguous(x, np.float64)
    y = atleast_1d_and_contiguous(y, np.float64)
    new_x = atleast_1d_and_contiguous(new_x, np.float64)

<<<<<<< HEAD
    assert len(y.shape) < 3, "function only works with 1D or 2D arrays"
=======
    if y.ndim > 2:
        raise ValueError("`linear` only works with 1-D or 2-D arrays.")
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    if len(y.shape) == 2:
        new_y = np.zeros((y.shape[0], len(new_x)), np.float64)
        for i in range(len(new_y)):
            _interpolate.loginterp_dddd(x, y[i], new_x, new_y[i])
    else:
        new_y = np.zeros(len(new_x), np.float64)
        _interpolate.loginterp_dddd(x, y, new_x, new_y)

    return new_y

<<<<<<< HEAD
def block_average_above(x, y, new_x):
    """ Linearly interpolates values in new_x based on the values in x and y

        Parameters
        ----------
        x
            1-D array
        y
            1-D or 2-D array
        new_x
            1-D array
=======

def block_average_above(x, y, new_x):
    """
    Linearly interpolates values in new_x based on the values in x and y.

    Parameters
    ----------
    x : array_like
        Independent values.
    y : array_like
        Dependent values.
    new_x : array_like
        The x values to interpolate y values.

>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    """
    bad_index = None
    x = atleast_1d_and_contiguous(x, np.float64)
    y = atleast_1d_and_contiguous(y, np.float64)
    new_x = atleast_1d_and_contiguous(new_x, np.float64)

<<<<<<< HEAD
    assert len(y.shape) < 3, "function only works with 1D or 2D arrays"
=======
    if y.ndim > 2:
        raise ValueError("`linear` only works with 1-D or 2-D arrays.")
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    if len(y.shape) == 2:
        new_y = np.zeros((y.shape[0], len(new_x)), np.float64)
        for i in range(len(new_y)):
            bad_index = _interpolate.block_averave_above_dddd(x, y[i],
                                                            new_x, new_y[i])
            if bad_index is not None:
                break
    else:
        new_y = np.zeros(len(new_x), np.float64)
        bad_index = _interpolate.block_average_above_dddd(x, y, new_x, new_y)

    if bad_index is not None:
        msg = "block_average_above cannot extrapolate and new_x[%d]=%f "\
              "is out of the x range (%f, %f)" % \
              (bad_index, new_x[bad_index], x[0], x[-1])
<<<<<<< HEAD
        raise ValueError, msg

    return new_y

def block(x, y, new_x):
    """ Essentially a step function.

        For each new_x[i], finds largest j such that
        x[j] < new_x[j], and returns y[j].
    """
    # find index of values in x that preceed values in x
=======
        raise ValueError(msg)

    return new_y


def block(x, y, new_x):
    """
    Essentially a step function.

    For each `new_x`, finds largest j such that``x[j] < new_x[j]`` and
    returns ``y[j]``.

    Parameters
    ----------
    x : array_like
        Independent values.
    y : array_like
        Dependent values.
    new_x : array_like
        The x values used to calculate the interpolated y.

    Returns
    -------
    block : ndarray
        Return array, of same length as `x_new`.

    """
    # find index of values in x that precede values in x
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    # This code is a little strange -- we really want a routine that
    # returns the index of values where x[j] < x[index]
    TINY = 1e-10
    indices = np.searchsorted(x, new_x+TINY)-1

    # If the value is at the front of the list, it'll have -1.
    # In this case, we will use the first (0), element in the array.
    # take requires the index array to be an Int
<<<<<<< HEAD
    indices = np.atleast_1d(np.clip(indices, 0, np.Inf).astype(np.int))
=======
    indices = np.atleast_1d(np.clip(indices, 0, np.Inf).astype(int))
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    new_y = np.take(y, indices, axis=-1)
    return new_y
