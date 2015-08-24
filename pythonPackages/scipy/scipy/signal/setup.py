#!/usr/bin/env python
<<<<<<< HEAD

def configuration(parent_package='',top_path=None):
=======
from __future__ import division, print_function, absolute_import

from scipy._build_utils import numpy_nodepr_api


def configuration(parent_package='', top_path=None):
>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
    from numpy.distutils.misc_util import Configuration

    config = Configuration('signal', parent_package, top_path)

    config.add_data_dir('tests')

    config.add_extension('sigtools',
<<<<<<< HEAD
                         sources=['sigtoolsmodule.c',
                                  'firfilter.c','medianfilter.c', 'lfilter.c.src',
                                  'correlate_nd.c.src'],
                         depends = ['sigtools.h'],
                         include_dirs=['.']
    )

    config.add_extension('spline',
        sources = ['splinemodule.c','S_bspline_util.c','D_bspline_util.c',
                   'C_bspline_util.c','Z_bspline_util.c','bspline_util.c'],
    )

    return config

=======
                         sources=['sigtoolsmodule.c', 'firfilter.c',
                                  'medianfilter.c', 'lfilter.c.src',
                                  'correlate_nd.c.src'],
                         depends=['sigtools.h'],
                         include_dirs=['.'],
                         **numpy_nodepr_api)

    config.add_extension('_spectral', sources=['_spectral.c'])
    config.add_extension('_max_len_seq_inner', sources=['_max_len_seq_inner.c'])

    spline_src = ['splinemodule.c', 'S_bspline_util.c', 'D_bspline_util.c',
                  'C_bspline_util.c', 'Z_bspline_util.c', 'bspline_util.c']
    config.add_extension('spline', sources=spline_src, **numpy_nodepr_api)

    return config


>>>>>>> 85b42d3bbdcef5cbe0fe2390bba8b3ff1608040b
if __name__ == '__main__':
    from numpy.distutils.core import setup
    setup(**configuration(top_path='').todict())
