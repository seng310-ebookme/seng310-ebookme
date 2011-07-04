
                ======================
                = EBookME & packager =
                ======================


EBookME je aplikace, kter� slou�� ke �ten� elektronick�ch knih na za��zen�ch
podporuj�c�ch Javu (JME) a implementuj�c�mi profil MIDP 1.0.

Aplikace pracuje korektn� tak� na syst�mech, kter� neposkytuj� souborov� syst�m
aplikaci a prohl�e� nem��e b�t samostatn� aplikace odd�len� od datov�ch
soubor�.

Aplikace vyu��v� mo�nosti Javy k ulo�en� dat spole�n� s v�konnou ��st� programu
v jednom arch�vu.

Pro vytvo�en� mobiln� aplikace sta�� spustit aplikaci "EBookME packager"
a bu� uv�st p��mo jm�no textov�ho souboru jako parametr nebo vyu��t intuitivn� GUI,
pro vytvo�en� va�� mobiln� knihy/knihovny.
Packager je standardn� Java (JSE) aplikace a ke sv�mu b�hu pot�ebuje prost�ed�
Java (JRE) ve verzi 1.4 nebo nov�j��.
(Ke sta�en� zdarma na adrese http://java.sun.com/)


==========
= Auto�i =
==========

Tom� Darmovzal
<tomas.darmovzal@seznam.cz>
(C) 2003-2005

Josef Cacek
<josef.cacek@gmail.com>
(C) 2005-2008

Ji�� Barto�
<juro.bartos@gmail.com>
(C) 2006

Eduard Hat�r
<eduard.hatar@gmail.com>
(C) 2007

==========================
= Popis aplikace EbookME =
==========================

- aplikace je ur�ena pro off-line �ten� - ve�ker� data
  elektronick�ho textu jsou sou��st� archivu aplikace,
  tj. p�i �ten� nen� pot�eba jak�koliv p�ipojen� k s�ti.
- aplikace je platform� nez�visl�, ale jestli�e pou��v�te mobiln�
  za��zen� od Siemensu nebo Nokie, m��ete vyu��t i propriet�rn�
  funkce, pro nastaven� podsv�tlen� displeje
- od ur�it� velikosti textu lep�� komprese ne� obvykl�
  PDB - textov� data jsou sou��st� JAR archivu aplikace,
  tak�e jsou komprimov�na b�nou ZIPovou kompres�, kter�
  na b�n�ch textov�ch datech dosahuje lep�� komprima�n�
  pom�r n� komprese pou�it� v PDB archivech
- programov� ��st zab�r� pouze cca 25 kB arch�vu JAR
- aplikace podporuje mnoho vstupn�ch znakov�ch sad
- aplikace m��e obsahovat i v�ce knih v jednom arch�vu (knihovna)
- aplikace umo��uje:
  - pohyb v textu po str�nk�ch (dop�edu/zp�t)
  - pohyb v textu po ��dc�ch (dop�edu/zp�t)
  - vkl�d�n� z�lo�ek
  - automatick� posun po ��dc�ch (autoscroll) s nastaven�m
    rychlosti posunu
  - nastaven� pou�it�ho fontu:
    - mal�/st�edn�/velk�
    - proporcion�ln�/neproporcion�ln�
    - oby�ejn�/tu�n�
  - nastaven� barvy textu a pozad� z n�kolika
    p�eddefinovan�ch variant
  - nastaven� aktu�ln� pozice v textu (v procentech
    celkov� velikosti textu)
  - zobrazen� informac� o textu, autorovi aplikace
    a n�pov�dy pro ovl�d�n� aplikace
  - ulo�en� n�sleduj�c�ch nastaven� p�i ukon�en� aplikace
    a znovuna�ten� p�i dal��m startu:
    - naposledy �ten� kniha
    - aktu�ln� pozice v knize
    - rychlost autoposuvu
    - vybran� font
    - zvolen� barevn� sch�ma
  - nastavit podsv�cen� displeje (pouze pro telefony Nokia a Siemens)
- aplikaci je mo�n� ovl�dat jak pomoc� b�n�ch numerick�ch
  tla��tek (mobiln� telefony) tak i pomoc� dotykov�ho
  displeje (PDA, smartphones)

===================================
= Popis aplikace EBookME Packager =
===================================

Packager je aplikace, kter� vytv��� elektronick� knihy pro mobiln� za��zen�.
M��e b�t spou�t�na bu� p��mo z p��kazov� ��dky (ovl�d�na pomoc� argument�),
nebo kdy� neposkytnete ��dn� parametr, spust� se plnohodnotn� GUI (Swing),
ve kter�m je pr�ce s programem velice intuitivn�.

Aplikace k zadan�mu vstupn�mu textu (text�m) vygeneruje dvojici
soubor� (s p�iponami .jad a .jar) obsahuj�c�ch zadan� text spole�n�
s aplikac� na jeho �ten�.

Pro spu�t�n� Packageru pou�ijte Java Runtime Environment ve verzi 1.4
nebo nov�j��.

=====================
= Pou�it� Packageru =
=====================

Spu�t�n�:
---------
Windows:
  EBookME.exe [argumenty]
nebo
  ebookme.bat [argumenty]
p��padn�
  ebookme.[jazyk].bat [argumenty]
Linux:
  ebookme.sh [argumenty]
V�echny syst�my s nainstalovanou javou:
  java -jar EBookME.jar [argumenty]


Argumenty:
----------

<file>
	N�zev vstupn�ho textov�ho souboru. Vstupn�ch text�
	m��e b�t uvedeno n�kolik, ke ka�d�mu z nich se vztahuj�
	volby uveden� p�ed n�m.


Volby (options):
----------------

--help
	Zobraz� n�pov�du

--version
	Zobraz� verzi aplikace

-f<soubor>
    Nahraje hodnoty parametr� z uveden�ho souboru <soubor>.
	B�hem startu se aplikace pokou�� na��st v�choz� hodnoty
	ze souboruje 'application.properties'.

-F<soubor>
	Stejn� jako volba -f, ale p�ed na�ten�m souboru jsou smaz�ny
	hodnoty v�ech parametr�.

-D<parametr=hodnota>
	P�id� nov� parametr, nebo zm�n� hodnotu existuj�c�ho.


Parametry Packageru:
--------------------
(Pro aktu�ln� seznam se pod�vejte na Appendix A v souboru EBookME.pdf)

name
	Jm�no knihy

description
	Popis knihy

charset
	K�dov�n� vstupn�ho souboru. M��ete pou��t i hodnotu 'default'
	pro nastaven� v�choz�ho k�dov�n� syst�mu.
	[v�choz� hodnota: 'default']

part
	Velikost bufferu v EbookME aplikaci
	(nejste-li si jisti, nem��te tento parametr).
	[v�choz� hodnota: 5000]

out
	Z�klad jm�na pro v�stupn� soubory.
	(nap�. p�i out='abc' budou vygenerov�ny soubory abc.jar and abc.jad)
	[v�choz� hodnota: 'ebook']



P��klady pou�it�:
-----------------

% java -jar EBookME.jar book.txt
	Generuje aplikaci (soubory ebook.jar, ebook.jad) ze vstupn�ho souboru book.txt, kter� m� v�choz� k�dov�n� syst�mu

% java -jar EBookME.jar "-Dname=Harry Potter 6" -Dcharset=windows-1250 -Dout=knihovna hp.txt -Dcharset=iso-8859-2 -Dname=Robocop robo.txt
	Generuje aplikaci knihovna.jar|jad, kter� obsahuje 2 knihy:
	  "Harry Potter 6" ze vstupn�ho souboru hp.txt s k�dov�n�m windows-1250
	  "Robocop" ze souboru robo.txt s k�dov�n�m iso-8859-2


==================================================================
Posledn� aktualizace                                    27.05.2008
==================================================================
