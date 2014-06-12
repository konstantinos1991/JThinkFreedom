JThinkFreedom
=============

JThinkFreedom is a framework for mapping behaviors (detected through any source, from camera to audio, to EEG) to reactions (e.g., from a computer click to... pulling a lever).

OpenCV - JavaCV
===============

JThinkFreedom runs on OpenCV (Open source Computer Vision). JavaCV is a Java implementation of OpenCV.

Building OpenCV on Linux
========================

<dl>
    <dt>Java, cmake, and ant required</dt>
    <dd>apt-get install openjdk-7-*</dd>
    <dd>apt-get install cmake</dd>
    <dd>apt-get install ant</dd>
    <dt>Set the JAVA_HOME variable for your environment</dt>
    <dd>nano $HOME/.bashrc</dd>
    <dd>Add the following line: export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/</dd>
    <dt>Download OpenCV (This guide will assume version 2.4.5)</dt>
    <dd>http://opencv.org/downloads.html</dd>
    <dd>tar -xzvf opencv-2.4.5.tar.gz</dd>
    <dd>Build OpenCV with Java support</dd>
    <dd>cd opencv-2.4.5/</dd>
    <dd>cmake cmake -D BUILD_SHARED_LIBS=OFF .</dd>
    <dd>make</dd>
    <dd>sudo make install</dd>
    <dt>ffmpeg libraries are not needed in this implementation</dt>
</dl>

JARS needed for JThinkFreedom
=============================
<dl>
	<dt>For convenience, here are the libraries you're going to need</dt>
	<dd>https://www.dropbox.com/sh/scn4pcz3hw46e1w/AAAf1PmuuaJkTMDfWCOkUACea</dd>
</dl>