The POI jar files were created on Feb. 17th, 2006, SVN revision 378471, with
jdk.version.source and jdk.version.target in the build file set to 1.4.

The reason for building our own jar files is that the official builds
(version 2.5.1-final) did not include all classed used in Aperture (esp. in
the implementation of our WordExtractor).

The created jar files were renamed to include the SVN revision number
to ensure full recreatability.

--
