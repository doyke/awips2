<<<<<<< HEAD
""" converters.py
"""

import common_info
import c_spec
=======
from __future__ import absolute_import, print_function

from . import common_info
from . import c_spec
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b

#----------------------------------------------------------------------------
# The "standard" conversion classes
#----------------------------------------------------------------------------

default = [c_spec.int_converter(),
           c_spec.float_converter(),
           c_spec.complex_converter(),
           c_spec.unicode_converter(),
           c_spec.string_converter(),
           c_spec.list_converter(),
           c_spec.dict_converter(),
           c_spec.tuple_converter(),
           c_spec.file_converter(),
           c_spec.instance_converter(),]
<<<<<<< HEAD
          #common_spec.module_converter()]
=======
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b

#----------------------------------------------------------------------------
# add numpy array converters to the default
# converter list.
#----------------------------------------------------------------------------
try:
<<<<<<< HEAD
    import standard_array_spec
=======
    from . import standard_array_spec
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    default.append(standard_array_spec.array_converter())
except ImportError:
    pass

#----------------------------------------------------------------------------
# add numpy scalar converters to the default
# converter list.
#----------------------------------------------------------------------------
try:
<<<<<<< HEAD
    import numpy_scalar_spec
=======
    from . import numpy_scalar_spec
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    default.append(numpy_scalar_spec.numpy_complex_scalar_converter())
except ImportError:
    pass

#----------------------------------------------------------------------------
# Add VTK support
#----------------------------------------------------------------------------

try:
<<<<<<< HEAD
    import vtk_spec
=======
    from . import vtk_spec
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    default.insert(0,vtk_spec.vtk_converter())
except IndexError:
    pass

#----------------------------------------------------------------------------
# Add "sentinal" catchall converter
#
# if everything else fails, this one is the last hope (it always works)
#----------------------------------------------------------------------------

default.append(c_spec.catchall_converter())

standard_info = [common_info.basic_module_info()]
standard_info += [x.generate_build_info() for x in default]

#----------------------------------------------------------------------------
# Blitz conversion classes
#
<<<<<<< HEAD
# same as default, but will convert numerix arrays to blitz C++ classes
# !! only available if numerix is installed !!
#----------------------------------------------------------------------------
try:
    import blitz_spec
=======
# same as default, but will convert numpy arrays to blitz C++ classes
#----------------------------------------------------------------------------
try:
    from . import blitz_spec
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    blitz = [blitz_spec.array_converter()] + default
    #-----------------------------------
    # Add "sentinal" catchall converter
    #
    # if everything else fails, this one
    # is the last hope (it always works)
    #-----------------------------------
    blitz.append(c_spec.catchall_converter())
except:
    pass
