#!/bin/sh

#------------------------------------------------------------------------------#
# [ShareGrid project - http://dcs.di.unipmn.it]
#
# Script for compiling a statically linked Blender executable.
#
# Author: Marco Guazzone (marco.guazzone@gmail.com)
#
# Copyright (C) 2007  Distributed Computing System (DCS) Group, Computer
# Science Department - University of Piemonte Orientale, Alessandria (Italy).
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published
# by the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#------------------------------------------------------------------------------#

##@{ [configurations]

BasePath=/usr/local/opt/Blender
RepoPath=$BasePath/repo
BuildPath=$BasePath/build
ConfPath=$BasePath/conf
InstallPath=$BasePath/subsys

##@} [configurations]

##@{ [vars]

cwd=`pwd`
old_ACLOCAL=
old_CFLAGS=
old_CXXFLAGS=
old_LDFLAGS=
old_LD_LIBRARY_PATH=
old_PATH=
old_PKG_CONFIG_PATH=
#ok=`true`
#ko=`false`
ok='1'
ko='0'

## X's vars
X_util_macros__app=util-macros-1.1.5
X_libXau__app=libXau-1.0.3
X_xproto__app=xproto-7.0.10
X_libXdmcp__app=libXdmcp-1.0.2
X_libpthread_stubs__app=libpthread-stubs-0.1
#X_xcb_proto__app=xcb-proto-1.0
X_xcb_proto__app=xcb-proto-1.1
#X_xcb__app=libxcb-1.0
X_libxcb__app=libxcb-1.1
X_xextproto__app=xextproto-7.0.2
X_kbproto__app=kbproto-1.0.3
X_inputproto__app=inputproto-1.4.2.1
X_xtrans__app=xtrans-1.0.4
X_libX11__app=libX11-1.1.3
X_libXext__app=libXext-1.0.2
X_libXi__app=libXi-1.1.3
X_libXv__app=libXv-1.0.3
X_libICE__app=libICE-1.0.4
X_libSM__app=libSM-1.0.3
X_libXt__app=libXt-1.0.4
X_libXxf86vm__app=libXxf86vm-1.0.1
X_libXrender__app=libXrender-0.9.4
X_randrproto__app=randrproto-1.2.1
X_libXrandr__app=libXrandr-1.2.2
## OpenGL's vars
OpenGL_libdrm__app=libdrm-2.3.0
#OpenGL_MesaLib__appver=7.0.1
OpenGL_MesaLib__appver=7.0.2
OpenGL_MesaLib__app=MesaLib-$OpenGL_MesaLib__appver
OpenGL_MesaLib__appdir=Mesa-$OpenGL_MesaLib__appver
## SDL's vars
SDL_libSDL__app=SDL-1.2.12
## JPEG's vars
JPEG_libJPEG__app=jpegsrc.v6b
JPEG_libJPEG__appdir=jpeg-6b
## PNG's vars
#PNG_libPNG__app=libpng-1.2.22
PNG_libPNG__app=libpng-1.2.23
## Python's vars
Python_Python__app=Python-2.5.1
Python_libpython__app=libpython2.5.a
## Blender's vars
Blender_blender__app=blender-2.45

##@} [vars]

##@{ [functions]

function init()
{
	# Creates directory layout
	#mkdir -p $BuildPath
	mkdir -p $InstallPath/bin
	mkdir -p $InstallPath/include
	mkdir -p $InstallPath/lib
	mkdir -p $InstallPath/man/man1
	mkdir -p $InstallPath/share

	# Saves the environment
	old_ACLOCAL=$ACLOCAL
	old_CFLAGS=$CFLAGS
	old_CXXFLAGS=$CXXFLAGS
	old_LDFLAGS=$LDFLAGS
	old_LD_LIBRARY_PATH=$LD_LIBRARY_PATH
	old_PATH=$PATH
	old_PKG_CONFIG_PATH=$PKG_CONFIG_PATH

	# Prepares the environment
	export ACLOCAL="aclocal -I '$InstallPath/share/aclocal'"
	export CFLAGS="$CFLAGS -I$InstallPath/include -I$InstallPath/include/X11 -L$InstallPath/lib64 -L$InstallPath/lib"
	export CXXFLAGS="$CXXFLAGS -I$InstallPath/include -I$InstallPath/include/X11 -L$InstallPath/lib64 -L$InstallPath/lib"
	export LDFLAGS="$LDFLAGS -L$InstallPath/lib64 -L$InstallPath/lib"
	export LD_LIBRARY_PATH="$InstallPath/lib64:$InstallPath/lib:$LD_LIBRARY_PATH"
	export PATH="$InstallPath/bin:$PATH"
	export PKG_CONFIG_PATH="$InstallPath/lib64/pkgconfig:$InstallPath/lib/pkgconfig:$PKG_CONFIG_PATH:/usr/lib64/pkgconfig:/usr/lib/pkgconfig"
}

function build_X()
{
	cd $cwd

	# util-macros
	app=$X_util_macros__app
	echo "Bulding X util-macros ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath"
	make && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libXau
	app=$X_libXau__app
	echo "Bulding X libXau ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# xproto
	app=$X_xproto__app
	echo "Bulding X xproto ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath"
	make && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libXdmcp
	app=$X_libXdmcp__app
	echo "Bulding X libXdmcp ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libpthread-stubs
	app=$X_libpthread_stubs__app
	echo "Bulding X libpthread-stubs ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# xcb-proto
	app=$X_xcb_proto__app
	echo "Bulding X xcb-proto ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath"
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libxcb
	app=$X_libxcb__app
	echo "Bulding X libxcb ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# xextproto
	app=$X_xextproto__app
	echo "Bulding X xextproto ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath"
	make && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# kbproto
	app=$X_kbproto__app
	echo "Bulding X kbproto ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath"
	make && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# inputproto
	app=$X_inputproto__app
	echo "Bulding X inputproto ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath"
	make && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# xtrans
	app=$X_xtrans__app
	echo "Bulding X xtrans ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath"
	make && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libX11
	app=$X_libX11__app
	echo "Bulding X libX11 ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libXext
	app=$X_libXext__app
	echo "Bulding X libXext ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libXi
	app=$X_libXi__app
	echo "Bulding X libXi ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libXv
	app=$X_libXv__app
	echo "Bulding X libXv ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libICE
	app=$X_libICE__app
	echo "Bulding X libICE ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libSM
	app=$X_libSM__app
	echo "Bulding X libSM ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libXt
	app=$X_libXt__app
	echo "Bulding X libXt ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libXxf86vm
	app=$X_libXxf86vm__app
	echo "Bulding X libXxf86vm ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libXrender
	app=$X_libXrender__app
	echo "Bulding X libXrender ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# randrproto
	app=$X_randrproto__app
	echo "Bulding X randrproto ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath"
	make && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# libXrandr
	echo "Bulding X libXrandr ($app) ..."
	app=$X_libXrandr__app
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	cd $cwd

	return $ok
}

function clean_X()
{
	cd $cwd

	# util-macros
	app=$X_util_macros__app
	echo "Cleaning X util-macros ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libXau
	app=$X_libXau__app
	echo "Cleaning X libXau ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# xproto
	app=$X_xproto__app
	echo "Cleaning X xproto ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libXdmcp
	app=$X_libXdmcp__app
	echo "Cleaning X libXdmcp ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libpthread-stubs
	app=$X_libpthread_stubs__app
	echo "Cleaning X libpthread-stubs ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# xcb-proto
	app=$X_xcb_proto__app
	echo "Cleaning X xcb-proto ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libxcb
	app=$X_libxcb__app
	echo "Cleaning X libxcb ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# xextproto
	app=$X_xextproto__app
	echo "Cleaning X xextproto ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# kbproto
	app=$X_kbproto__app
	echo "Cleaning X kbproto ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# inputproto
	app=$X_inputproto__app
	echo "Cleaning X inputproto ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# xtrans
	app=$X_xtrans__app
	echo "Cleaning X xtrans ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libX11
	app=$X_libX11__app
	echo "Cleaning X libX11 ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libXext
	app=$X_libXext__app
	echo "Cleaning X libXext ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libXi
	app=$X_libXi__app
	echo "Cleaning X libXi ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libXv
	app=$X_libXv__app
	echo "Cleaning X libXv ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libICE
	app=$X_libICE__app
	echo "Cleaning X libICE ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libSM
	app=$X_libSM__app
	echo "Cleaning X libSM ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libXt
	app=$X_libXt__app
	echo "Cleaning X libXt ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libXxf86vm
	app=$X_libXxf86vm__app
	echo "Cleaning X libXxf86vm ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libXrender
	app=$X_libXrender__app
	echo "Cleaning X libXrender ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# randrproto
	app=$X_randrproto__app
	echo "Cleaning X randrproto ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# libXrandr
	app=$X_libXrandr__app
	echo "Cleaning X libXrandr ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	cd $cwd

	return $ok
}

function build_OpenGL()
{
	cd $cwd

	# libdrm
	app=$OpenGL_libdrm__app
	echo "Building OpenGL libdrm ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make check && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	# MesaLib
	app=$OpenGL_MesaLib__app
	echo "Building OpenGL MesaLib ($app) ..."
	appdir=$OpenGL_MesaLib__appdir
	cd $BuildPath
	if [ ! -e "$appdir" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $appdir
	make uninstall
	if [ ! -e "configs/default.orig" ]; then
		cp configs/default configs/default.orig
	fi
	cp $ConfPath/Mesa/configs/default.$OpenGL_MesaLib__appver configs/default
	if [ ! -e "configs/linux.orig" ]; then
		cp configs/linux configs/linux.orig
	fi
	cp $ConfPath/Mesa/configs/linux.$OpenGL_MesaLib__appver configs/linux
	if [ ! -e "configs/linux-static.orig" ]; then
		cp configs/linux-static configs/linux-static.orig
	fi
	cp $ConfPath/Mesa/configs/linux-static.$OpenGL_MesaLib__appver configs/linux-static
	if [ ! -e "src/glw/glw.pc.in" ]; then
		# Fix a broken MesaLib distribution (glw.pc.in is missing)
		cp $ConfPath/Mesa/src/glw/glw.pc.in.$OpenGL_MesaLib__appver src/glw/glw.pc.in
	fi
	make realclean
	make linux-static && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	cd $cwd

	return $ok
}

function clean_OpenGL()
{
	cd $cwd

	# libdrm
	app=$OpenGL_libdrm__app
	echo "Cleaning OpenGL libdrm ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	# MesaLib
	app=$OpenGL_MesaLib__app
	echo "Cleaning OpenGL MesaLib ($app) ..."
	appdir=$OpenGL_MesaLib__appdir
	cd $BuildPath
	if [ -e "$appdir" ]; then
		cd $appdir
		make uninstall
		make realclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	cd $cwd

	return $ok
}

function build_SDL()
{
	cd $cwd

	# libSDL
	app=$SDL_libSDL__app
	echo "Building SDL libSDL ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar zxvf $RepoPath/$app.tar.gz
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared --disable-x11-shared --disable-audio
	make && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	cd $cwd

	return $ok
}

function clean_SDL()
{
	cd $cwd

	# libSDL
	app=$SDL_libSDL__app
	echo "Cleaning SDL libSDL ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	cd $cwd

	return $ok
}

function build_libJPEG()
{
	cd $cwd

	# libjpeg
	app=$JPEG_libJPEG__app
	echo "Building JPEG libJPEG ($app) ..."
	appdir=$JPEG_libJPEG__appdir
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar zxvf $RepoPath/$app.tar.gz
	fi
	cd $appdir
	make uninstall
	make distclean
	##@{ Fix for 64bit platforms
	cp /usr/share/libtool/config.guess .
	cp /usr/share/libtool/config.sub .
	##@} Fix for 64bit platforms
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make "test" && make install && make install-lib
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	cd $cwd

	return $ok
}

function clean_libJPEG()
{
	cd $cwd

	# libjpeg
	app=$JPEG_libJPEG__app
	echo "Cleaning JPEG libJPEG ($app) ..."
	appdir=$JPEG_libJPEG__appdir
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $appdir
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	cd $cwd

	return $ok
}

function build_libPNG()
{
	cd $cwd

	# libpng
	app=$PNG_libPNG__app
	echo "Building PNG libPNG ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make "test" && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	cd $cwd

	return $ok
}

function clean_libPNG()
{
	cd $cwd

	# libpng
	app=$PNG_libPNG__app
	echo "Cleaning PNG libPNG ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	cd $cwd

	return $ok
}

function build_Python()
{
	cd $cwd

	# Python
	app=$Python_Python__app
	applib=$Python_libpython__app
	echo "Building Python python ($app/$applib) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar jxvf $RepoPath/$app.tar.bz2
	fi
	cd $app
	make uninstall
	make distclean
	./configure --prefix="$InstallPath" --enable-static --disable-shared
	make && make "test" && make install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi
	cp $applib $InstallPath/lib/.

	cd $cwd

	return $ok
}

function clean_Python()
{
	cd $cwd

	# Python
	app=$Python_Python__app
	echo "Cleaning Python Python ($app) ..."
	applib=$Python_libpython__app
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		make uninstall
		make distclean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
		rm $InstallPath/lib/$applib
	fi

	cd $cwd

	return $ok
}

function build_Blender()
{
	cd $cwd

	# Blender
	app=$Blender_blender__app
	echo "Building Blender blender ($app) ..."
	cd $BuildPath
	if [ ! -e "$app" ]; then
		tar zxvf $RepoPath/$app.tar.gz
	fi
	cd $app
	scons uninstall
	scons -c
	scons clean
	if [ ! -e "config/linux2-config.py.orig" ]; then
		cp config/linux2-config.py config/linux2-config.py.orig
	fi
	> config/linux2-config.py
	cp $ConfPath/Blender/user-config.py .
	#scons
	scons BF_QUIET=0 >scons.out 2>&1
	scons install
	if [ "$?" != "0" ]; then
		echo "Error while building '$app'"
		return $ko
	fi

	cd $cwd

	return $ok
}

function clean_Blender()
{
	cd $cwd

	# Blender
	app=$Blender_blender__app
	echo "Cleaning Blender blender ($app) ..."
	cd $BuildPath
	if [ -e "$app" ]; then
		cd $app
		scons uninstall
		scons -c
		scons clean
		if [ "$?" != "0" ]; then
			echo "Error while cleaning '$app'"
			return $ko
		fi
	fi

	cd $cwd

	return $ok
}

function buildAll()
{
if [ "0" == "1" ]; then

	echo "Building X ..."
	build_X
	if [ "$?" == "$ko" ]; then
		echo "Error while building X"
		return $ko
	fi

	echo "Building OpenGL ..."
	build_OpenGL
	if [ "$?" == "$ko" ]; then
		echo "Error while building OpenGL"
		return $ko
	fi

	echo "Building SDL ..."
	build_SDL
	if [ "$?" == "$ko" ]; then
		echo "Error while building SDL"
		return $ko
	fi

	echo "Building libJPEG ..."
	build_libJPEG
	if [ "$?" == "$ko" ]; then
		echo "Error while building libJPEG"
		return $ko
	fi

	echo "Building libPNG ..."
	build_libPNG
	if [ "$?" == "$ko" ]; then
		echo "Error while building libPNG"
		return $ko
	fi

fi

	echo "Building Python ..."
	build_Python
	if [ "$?" == "$ko" ]; then
		echo "Error while building Python"
		return $ko
	fi

	echo "Building Blender ..."
	build_Blender
	if [ "$?" == "$ko" ]; then
		echo "Error while building Blender"
		return $ko
	fi

	return $ok
}

function cleanAll()
{
	clean_Blender
	if [ "$?" == "$ko" ]; then
		echo "Error while cleaning Blender"
#		return $ko
	fi

	clean_Python
	if [ "$?" == "$ko" ]; then
		echo "Error while cleaning Python"
#		return $ko
	fi

	clean_libPNG
	if [ "$?" == "$ko" ]; then
		echo "Error while cleaning libPNG"
#		return $ko
	fi

	clean_libJPEG
	if [ "$?" == "$ko" ]; then
		echo "Error while cleaning libJPEG"
#		return $ko
	fi

	clean_SDL
	if [ "$?" == "$ko" ]; then
		echo "Error while cleaning SDL"
#		return $ko
	fi

	clean_OpenGL
	if [ "$?" == "$ko" ]; then
		echo "Error while cleaning OpenGL"
#		return $ko
	fi

	clean_X
	if [ "$?" == "$ko" ]; then
		echo "Error while cleaning X"
#		return $ko
	fi

	 rm -rf	$BuildPath/* \
		$InstallPath/bin/* \
		$InstallPath/include/* \
		$InstallPath/lib/pkgconfig/* \
		$InstallPath/lib/python2.5/* \
		$InstallPath/libexec/* \
		$InstallPath/lib64/*

	return $ok
}

function finit()
{
	# Restores the environment
	export ACLOCAL=$old_ACLOCAL
	export CFLAGS=$old_CFLAGS
	export CXXFLAGS=$old_CXXFLAGS
	export LDFLAGS=$old_LDFLAGS
	export LD_LIBRARY_PATH=$old_LD_LIBRARY_PATH
	export PATH=$old_PATH
	export PKG_CONFIG_PATH=$old_PKG_CONFIG_PATH

	# Returns to the original directory
	cd $cwd
}

##@} [functions]

##@{ [main]

RETVAL=$ok

case "$1" in
	install)
		init
		buildAll
		RETVAL=$?
		finit
		;;
	uninstall)
		init
		cleanAll
		RETVAL=$?
		finit
		;;
	*)
		echo $"Usage: $0 {install|uninstall}"
		exit -1
		;;
esac

if [ "$RETVAL" == "$ok" ]; then
	echo "All successfully done!"
	RETVAL=0
else
	echo "An error is occurred!"
	RETVAL=1
fi

exit $RETVAL

##@} [main]
