<<<<<<< HEAD
#
# fftpack - Discrete Fourier Transform algorithms.
#
# Created: Pearu Peterson, August,September 2002

from info import __all__,__doc__

from fftpack_version import fftpack_version as __version__

from basic import *
from pseudo_diffs import *
from helper import *
=======
"""
==================================================
Discrete Fourier transforms (:mod:`scipy.fftpack`)
==================================================

Fast Fourier Transforms (FFTs)
==============================

.. autosummary::
   :toctree: generated/

   fft - Fast (discrete) Fourier Transform (FFT)
   ifft - Inverse FFT
   fft2 - Two dimensional FFT
   ifft2 - Two dimensional inverse FFT
   fftn - n-dimensional FFT
   ifftn - n-dimensional inverse FFT
   rfft - FFT of strictly real-valued sequence
   irfft - Inverse of rfft
   dct - Discrete cosine transform
   idct - Inverse discrete cosine transform
   dst - Discrete sine transform
   idst - Inverse discrete sine transform

Differential and pseudo-differential operators
==============================================

.. autosummary::
   :toctree: generated/

   diff - Differentiation and integration of periodic sequences
   tilbert - Tilbert transform:         cs_diff(x,h,h)
   itilbert - Inverse Tilbert transform: sc_diff(x,h,h)
   hilbert - Hilbert transform:         cs_diff(x,inf,inf)
   ihilbert - Inverse Hilbert transform: sc_diff(x,inf,inf)
   cs_diff - cosh/sinh pseudo-derivative of periodic sequences
   sc_diff - sinh/cosh pseudo-derivative of periodic sequences
   ss_diff - sinh/sinh pseudo-derivative of periodic sequences
   cc_diff - cosh/cosh pseudo-derivative of periodic sequences
   shift - Shift periodic sequences

Helper functions
================

.. autosummary::
   :toctree: generated/

   fftshift - Shift the zero-frequency component to the center of the spectrum
   ifftshift - The inverse of `fftshift`
   fftfreq - Return the Discrete Fourier Transform sample frequencies
   rfftfreq - DFT sample frequencies (for usage with rfft, irfft)

Note that ``fftshift``, ``ifftshift`` and ``fftfreq`` are numpy functions
exposed by ``fftpack``; importing them from ``numpy`` should be preferred.

Convolutions (:mod:`scipy.fftpack.convolve`)
============================================

.. module:: scipy.fftpack.convolve

.. autosummary::
   :toctree: generated/

   convolve
   convolve_z
   init_convolution_kernel
   destroy_convolve_cache

"""

# List of possibly useful functions in scipy.fftpack._fftpack:
#   drfft
#   zfft
#   zrfft
#   zfftnd
#   destroy_drfft_cache
#   destroy_zfft_cache
#   destroy_zfftnd_cache

from __future__ import division, print_function, absolute_import


__all__ = ['fft','ifft','fftn','ifftn','rfft','irfft',
           'fft2','ifft2',
           'diff',
           'tilbert','itilbert','hilbert','ihilbert',
           'sc_diff','cs_diff','cc_diff','ss_diff',
           'shift',
           'rfftfreq',
           'fftfreq', 'fftshift', 'ifftshift'
           ]

from .fftpack_version import fftpack_version as __version__

from .basic import *
from .pseudo_diffs import *
from .helper import *
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b

from numpy.dual import register_func
for k in ['fft', 'ifft', 'fftn', 'ifftn', 'fft2', 'ifft2']:
    register_func(k, eval(k))
del k, register_func

<<<<<<< HEAD
from realtransforms import *
__all__.extend(['dct', 'idct'])
=======
from .realtransforms import *
__all__.extend(['dct', 'idct', 'dst', 'idst'])
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b

from numpy.testing import Tester
test = Tester().test
bench = Tester().bench
