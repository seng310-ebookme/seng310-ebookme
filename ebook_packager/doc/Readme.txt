
                ======================
                = EBookME & packager =
                ======================


EBookME is application for reading e-books on the devices
which supports Java (JME) with MIDP 1.0 profile.

Application works corretly also on devices which doesn't
provide filesystem to application and a reader can't
be standalone application separated from data files.

Application uses adventages of Java and stores application
and data in one archive file.

To create archive files for mobile phones you have to run
packager application on your computer. Packager
is a standard Java (JSE) application, which needs to run Java
Runtime Environment (JRE) in version 1.4 or newer.
(see http://java.sun.com/)


==================================
=          Authors               =
==================================

Tomas Darmovzal
<tomas.darmovzal@seznam.cz>
(C) 2003-2005

Josef Cacek
<josef.cacek@gmail.com>
(C) 2005-2008

Jiri Bartos
<juro.bartos@gmail.com>
(C) 2006

Eduard Hatar
<eduard.hatar@gmail.com>
(C) 2007

==========================
= Description of EbookME =
==========================

- application is intended for offline reading, all data
  of electronic text are part of an application archive
  (i.e. you don't need to be conected to network during reading)
- application is platform independent, but if you have Siemens
  or Nokia mobile phone, you can use 'platform-dependent' function
  for backlight display (for Siemens it only takes enabled/disabled,
  nokia provides functionality to set intensity of backlight)
- text data are part of JAR archive, it means, that it's compressed
  with ZIP compression, which is for normal text data more efficient
  than e.g. PDB-format compression
- application part takes about 25 kB of JAR archive
- application support many of input charatecter encodings
- application can contain more different e-books (it's like
  library)
- application provide:
  - move text page by page (Fw/Bw)
  - move text line by line (Fw/Bw)
  - add bookmarks to save position in text
  - automatic move line by line (autoscroll) with setting
    speed of scrolling
  - select of text font:
    - small/middle/large
    - proporcional/monospace
    - normal/bold
  - select of text and background color from several
    predefined choices
  - set actual position in text (in percents)
  - display information about e-book, author of application and help
  - save when application is finished and reload during next start:
    - actual position in text
    - actual speed of autoscroll
    - selected font
    - selected color scheme
  - enable display backlight (only for Nokia and Siemens phones)
- application can be control by standard numeric keypad or by touch-screen
  (PDA, smartphones)

===========================
= Description of Packager =
===========================

Application generates two files JAD+JAR for input plain-text(s). JAR then contains
text and EbookME application, JAD is descriptor (text file) for JAR application.

You have to use Java Runtime Environment in version 1.4 or newer.

Application is driven by command line or simple Swing GUI.

==================
= Packager usage =
==================

Launching:
----------
Windows:
  EBookME.exe [arguments]
or
  ebookme.bat [arguments]
or another localized versions
  ebookme.[language].bat [arguments]
Linux:
  ebookme.sh [arguments]
All systems with java installed:
  java -jar EBookME.jar [arguments]

Application started without arguments displays comprehensive Swing GUI.
Use command line arguments only if you want to work in console (batch mode).  

Arguments:
-----------

[file]
	Name of input text file. There can be several texts and
	for each are used options before it.

Options:
--------

--help
	Prints help screen

--version
	Prints version of EbookME

-f<filename>
	Loads initial parameters from property file <filename>.
	During start, application tries to read file 'application.properties'
	and load properties from it. You can rewrite this properties (or add new ones)
	e.g. by loading new property file.


-F<filename>
	The same as -f option, but it clears all properties before loading new file.

-D<property=value>
	Adds or replaces value of property with given name.


Basic properties:
-----------------
(For full actual list see to Appendix A in a file EBookME.pdf)

name
	Book name

description
	Book description

charset
	Character encoding of an input file. You can use value 'default' for system default encoding.
	[default value: 'default']

part
	Size of book part and of EBookME buffer
	[default value: 5000]

out
	Base for output files names (i.e. if out='abc', output files will be abc.jar and abc.jad)
	[default value: 'ebook']



Samples of usage:
-----------------

% ebookme.bat book.txt
	Generates ebook (files ebook.jar, ebook.jad), it uses system default encoding and default part size (5000))

% ebookme.bat -Dcharset=iso-8859-1 -Dout=library "-Dname=Harry Potter" hp.txt -Dname=Robocop robo.txt
	Generates files library.jar|jad, which contains 2 books "Harry Potter" and "Robocop", both have input
	encoding iso-8859-1.


==================================================================
Last update                                             05/27/2008
==================================================================
