
                ======================
                = EBookME & packager =
                ======================


EBookME je aplikace, která slouží ke ètení elektronických knih na zaøízeních
podporujících Javu (JME) a implementujícími profil MIDP 1.0.

Aplikace pracuje korektnì také na systémech, které neposkytují souborový systém
aplikaci a prohlížeè nemùže být samostatná aplikace oddìlená od datových
souborù.

Aplikace využívá možnosti Javy k uložení dat spoleènì s výkonnou èástí programu
v jednom archívu.

Pro vytvoøení mobilní aplikace staèí spustit aplikaci "EBookME packager"
a buï uvést pøímo jméno textového souboru jako parametr nebo využít intuitivní GUI,
pro vytvoøení vaší mobilní knihy/knihovny.
Packager je standardní Java (JSE) aplikace a ke svému bìhu potøebuje prostøedí
Java (JRE) ve verzi 1.4 nebo novìjší.
(Ke stažení zdarma na adrese http://java.sun.com/)


==========
= Autoøi =
==========

Tomáš Darmovzal
<tomas.darmovzal@seznam.cz>
(C) 2003-2005

Josef Cacek
<josef.cacek@gmail.com>
(C) 2005-2008

Jiøí Bartoš
<juro.bartos@gmail.com>
(C) 2006

Eduard Határ
<eduard.hatar@gmail.com>
(C) 2007

==========================
= Popis aplikace EbookME =
==========================

- aplikace je urèena pro off-line ètení - veškerá data
  elektronického textu jsou souèástí archivu aplikace,
  tj. pøi ètení není potøeba jakékoliv pøipojení k síti.
- aplikace je platformì nezávislá, ale jestliže používáte mobilní
  zaøízení od Siemensu nebo Nokie, mùžete využít i proprietární
  funkce, pro nastavení podsvìtlení displeje
- od urèité velikosti textu lepší komprese než obvyklé
  PDB - textová data jsou souèástí JAR archivu aplikace,
  takže jsou komprimována bìžnou ZIPovou kompresí, která
  na bìžných textových datech dosahuje lepší komprimaèní
  pomìr nìž komprese použitá v PDB archivech
- programová èást zabírá pouze cca 25 kB archívu JAR
- aplikace podporuje mnoho vstupních znakových sad
- aplikace mùže obsahovat i více knih v jednom archívu (knihovna)
- aplikace umožòuje:
  - pohyb v textu po stránkách (dopøedu/zpìt)
  - pohyb v textu po øádcích (dopøedu/zpìt)
  - vkládání záložek
  - automatický posun po øádcích (autoscroll) s nastavením
    rychlosti posunu
  - nastavení použitého fontu:
    - malý/støední/velký
    - proporcionální/neproporcionální
    - obyèejný/tuèný
  - nastavení barvy textu a pozadí z nìkolika
    pøeddefinovaných variant
  - nastavení aktuální pozice v textu (v procentech
    celkové velikosti textu)
  - zobrazení informací o textu, autorovi aplikace
    a nápovìdy pro ovládání aplikace
  - uložení následujících nastavení pøi ukonèení aplikace
    a znovunaètení pøi dalším startu:
    - naposledy ètená kniha
    - aktuální pozice v knize
    - rychlost autoposuvu
    - vybraný font
    - zvolené barevné schéma
  - nastavit podsvícení displeje (pouze pro telefony Nokia a Siemens)
- aplikaci je možné ovládat jak pomocí bìžných numerických
  tlaèítek (mobilní telefony) tak i pomocí dotykového
  displeje (PDA, smartphones)

===================================
= Popis aplikace EBookME Packager =
===================================

Packager je aplikace, která vytváøí elektronické knihy pro mobilní zaøízení.
Mùže být spouštìna buï pøímo z pøíkazové øádky (ovládána pomocí argumentù),
nebo když neposkytnete žádný parametr, spustí se plnohodnotné GUI (Swing),
ve kterém je práce s programem velice intuitivní.

Aplikace k zadanému vstupnímu textu (textùm) vygeneruje dvojici
souborù (s pøiponami .jad a .jar) obsahujících zadaný text spoleènì
s aplikací na jeho ètení.

Pro spuštìní Packageru použijte Java Runtime Environment ve verzi 1.4
nebo novìjší.

=====================
= Použití Packageru =
=====================

Spuštìní:
---------
Windows:
  EBookME.exe [argumenty]
nebo
  ebookme.bat [argumenty]
pøípadnì
  ebookme.[jazyk].bat [argumenty]
Linux:
  ebookme.sh [argumenty]
Všechny systémy s nainstalovanou javou:
  java -jar EBookME.jar [argumenty]


Argumenty:
----------

<file>
	Název vstupního textového souboru. Vstupních textù
	mùže být uvedeno nìkolik, ke každému z nich se vztahují
	volby uvedené pøed ním.


Volby (options):
----------------

--help
	Zobrazí nápovìdu

--version
	Zobrazí verzi aplikace

-f<soubor>
    Nahraje hodnoty parametrù z uvedeného souboru <soubor>.
	Bìhem startu se aplikace pokouší naèíst výchozí hodnoty
	ze souboruje 'application.properties'.

-F<soubor>
	Stejné jako volba -f, ale pøed naètením souboru jsou smazány
	hodnoty všech parametrù.

-D<parametr=hodnota>
	Pøidá nový parametr, nebo zmìní hodnotu existujícího.


Parametry Packageru:
--------------------
(Pro aktuální seznam se podívejte na Appendix A v souboru EBookME.pdf)

name
	Jméno knihy

description
	Popis knihy

charset
	Kódování vstupního souboru. Mùžete použít i hodnotu 'default'
	pro nastavení výchozího kódování systému.
	[výchozí hodnota: 'default']

part
	Velikost bufferu v EbookME aplikaci
	(nejste-li si jisti, nemìòte tento parametr).
	[výchozí hodnota: 5000]

out
	Základ jména pro výstupní soubory.
	(napø. pøi out='abc' budou vygenerovány soubory abc.jar and abc.jad)
	[výchozí hodnota: 'ebook']



Pøíklady použití:
-----------------

% java -jar EBookME.jar book.txt
	Generuje aplikaci (soubory ebook.jar, ebook.jad) ze vstupního souboru book.txt, který má výchozí kódování systému

% java -jar EBookME.jar "-Dname=Harry Potter 6" -Dcharset=windows-1250 -Dout=knihovna hp.txt -Dcharset=iso-8859-2 -Dname=Robocop robo.txt
	Generuje aplikaci knihovna.jar|jad, která obsahuje 2 knihy:
	  "Harry Potter 6" ze vstupního souboru hp.txt s kódováním windows-1250
	  "Robocop" ze souboru robo.txt s kódováním iso-8859-2


==================================================================
Poslední aktualizace                                    27.05.2008
==================================================================
