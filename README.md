---
section: 32
x-masysma-name: dirstat
title: Ma_Sys.ma DirStat 1 und 2
date: 2019/12/06 13:16:32
lang: en-US
author: ["Linux-Fan, Ma_Sys.ma (Ma_Sys.ma@web.de)"]
keywords: ["dirstat2", "DirStat", "Dir", "Stat", "scan"]
x-masysma-version: 2.0.0
x-masysma-repository: https://www.github.com/m7a/bo-dirstat
x-masysma-owned: 1
x-masysma-copyright: |
  Copyright (c) 2014, 2019 Ma_Sys.ma.
  For further info send an e-mail to Ma_Sys.ma@web.de.
---
Summary
=======

DirStat 2 allows the state of a filesystem hierarchy to be recorded in a
PostgreSQL database and includes a GUI to connect to this database in order
to perform analyses in terms of file types and file sizes.

This package includes DirStat 1 which provides a subset of the functionality
(a fixed analysis) in a less stable but still useful commandline application
without the need to acquire data into a database first.

DirStat 1 Description (in German)
=================================

Mit DirStat konnte man statistische Informationen über die in einem Ordner
enthaltenen Dateien anfertigen. Dabei zeigte das Programm auch Statistiken
nach Dateitypen an. Mit einer komplexen Einstellungsdatei ließen sich alle
Dateitypen einstellen, sowie festlegen, welche Statistiken für diese
Typen und für die Gesamtauswertung angezeigt werden sollten. Als Nebenfunktion
konnte das Programm auch versuchen den Dateityp einer angegebenen Datei zu
ermitteln. Dies war allerdings wegen des aufwändigen Ladevorgangs der Datenbank
nicht für allzu häufige Abfragen geeignet. DirStat konnte auch Informationen wie
die Anzahl leerer Dateien ermitteln, die sich eignen, um ein (vermülltes)
Verzeichnis zu finden und aufzuräumen.

Das Programm war nicht ganz fertig, es fehlte noch ein Abbruchsmechanismus
und eine Funktion die es ermöglichen sollte, ein zu einer Datei passendes
Programm zu öffnen. Zusätzlich musste das Programm noch hinsichtlich der
Geschwindikeit optimiert werden. Trotz der Unfertigkeit war das Programm
schon Praxistauglich -- die wichtigsten Funktionen arbeiteten schon korrekt.

Die beiliegende Konfigurationsdatei zeigte eine sehr ausführliche Statistik und
war (wie auch der Rest des Programmes) noch nicht ganz fertig.

Beispiel für eine (sehr einfache) Konfigurationsdatei, die nur die
Gesamtstatistik anzeigt und alle anderen Dateien außer `.exe` Dateien
als unbekannt meldet:

~~~{.xml}
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!--
	Ma_Sys.ma Dir Stat Configuration File, Copyright (c) 2011 Ma_Sys.ma.
	For further info send an e-mail to Ma_Sys.ma@web.de
-->
<!DOCTYPE stats [
<!ELEMENT stats (type, format+)>
<!ATTLIST stats version CDATA #FIXED "1.0">
<!ELEMENT type (type)*>
<!ATTLIST type title CDATA #REQUIRED>
<!ATTLIST type pattern CDATA #IMPLIED>
<!ATTLIST type sort (category|extension|extension-regex|regex|name) "extension">
<!ATTLIST type emr (true|false) "false">
<!ATTLIST type stats (end|big|small|sub) "sub">
<!ELEMENT format (format)*>
<!ATTLIST format title CDATA #IMPLIED>
<!ATTLIST format data CDATA #IMPLIED>
<!ATTLIST format value (title|hidden|executable|unreadable|readonly|unreadable-dirs|zero-byte|non-files|folders|files-between|max-depth|biggest-folder-size|biggest-folder-files|biggest-file|doubles|size|files|time|link-traps|avg-files-per-dir|avg-file-size|avg-speed-files-per-second|avg-speed-MiB-per-second) "title">
<!ATTLIST format describes (end|big|small|sub) "sub">
]>

<stats>
	<type title="all" stats="end" sort="category">
		<type title="Windows Programm" pattern="exe" />
	</type>
	<format describes="big">
		<format title="Dateien" value="files" />
	</format>
	<format describes="sub">
		<format title="Dateien"   value="files" />
		<format title="Groesse"   value="size" />
	</format>
	<format describes="end">
		<format title="Dateien"           value="files" />
		<format title="Ordner"            value="folders" />
		<format title="Groesse"           value="size" />
		<format title="Versteckt"         value="hidden" />
		<format title="Aufuehrbar"        value="executable" />
		<format title="Unlesbar"          value="unreadable" />
		<format title="Schreibgeschuetzt" value="readonly" />
		<format title="Groesste Datei"    value="biggest-file" />
		<format title="Leere Dateien"     value="zero-byte" />
	</format>
</stats>
~~~

Ein Aufruf von DirStat mit dieser Konfigurationsdatei über den gesamten
Ma_Sys.ma Datenbestand gab folgende Ausgabe (unbekannte Dateitypen wurden
gekürzt):

	Ma_Sys.ma Dir Stat, Copyright (c) 2011 Ma_Sys.ma.
	For further info send an e-mail to Ma_Sys.ma@web.de.
	
	Parsing XML Configuration from conf.xml...
	Parsing complete.
	
	Creating statistics for /data...
	scan() 1, (1/s), 87 MiB free
	scan() 39076, (39075/s), 123 MiB free
	Progressed 70189 scans, 81 MiB free
	
	all
	  Windows Programm     e=exe              Dateien=53    Größe=465.99 MiB
	  Unknown Type
	    *.gz               e=gz               Dateien=58    Größe=38.89 MiB
	     ... Aus Platzgründen wurde der Rest der Liste ausgelassen ...
	    *.sa4              e=sa4              Dateien=3     Größe=0.45 MiB
	  Dateien              63786
	  Unknown Type Without Extension
	  Dateien              2194
	Dateien                66033
	Ordner                 4155
	Größe                  19052.55 MiB
	Versteckt              181
	Auführbar              63965
	Unlesbar               0
	Schreibgeschützt       44021
	Größte Datei           4290.06 MiB
	Leere Dateien          168

Obwohl DirStat 1 nie fertig wurde, wurde eine zweite Version DirStat 2
geschrieben, die auch erfolgreich fertiggestellt wurde.

DirStat 2
=========

## Introduction

DirStat 2 is a powerful tool to analyze directory structures. If you encounter
a HDD filled with many files, folders and subfolders you might want to know
which files are the largest, which file types take up the most space, which user
names the files belong to or other questions which you can not answer directly
using the utilities provided by the platform especially on Windows systems.

On Linux you could write scripts for that purpose but filenames can be very
troublesome in scripts and listing all that information neatly will require
a complex set of scripts still inferior to DirStat 2.

DirStat 2 tries to address these questions: You can scan a directory structure
(refer to _Scanning_) and analyze it later -- even on a separate system if
you prefer your known working environment. The file metadata is stored in a
PostgreSQL database which allows the user to make arbitrary queries to get
whichever information is required. The DirStat 2 GUI (see _Evaluation_)
presents important information at one sight and can be extended via user-written
JavaScripts to allow very specific views on the data.

## Credits

This program uses PostgreSQL's Java Driver and includes Netbeans' _Outline_
component in file `dirstat2/lib/org-netbeans-swing-outline.jar`. See
`dirstat2/lib/cddl.txt` for license details.

## Quickstart with Docker

Assuming you do not want to install neither PostgreSQL nor DirStat, it is also
possible to run it portably by means of PostgreSQL's docker image:

 1. Compile DirStat 2 by issuing `ant jar`
 2. Start database (non-persistently) with Docker
    `docker run -p 127.0.0.1:5432:5432 -e POSTGRES_PASSWORD=testwort -e POSTGRES_USER=linux-fan -e POSTGRES_DB=masysma_dirstat --rm -it postgres:12`
 3. Perform a scan
    `java -cp /usr/share/java/postgresql.jar:./dirstat2.jar ma.dirstat.Main --scan --db=INSECURE:linux-fan:testwort@127.0.0.1:5432 --name=scan1 --src=/data/main/mdvl`
 4. View scan in GUI
    `java -cp /usr/share/java/postgresql.jar:lib/org-netbeans-swing-outline.jar:./dirstat2.jar ma.dirstat.Main --eval --db=INSECURE:linux-fan:testwort@127.0.0.1:5432 --name=scan1`
 5. _perform data-analysis here_
 6. Exit GUI and Container to clean up all data,
    call `ant dist-clean` to reset repository to initial state.

Note that this _INSECURE_ here is not so insecure as the database is on the
local machine. So as long as you are the only user, this invocation is safe to
use.

## Prerequisites and Setup

Depending on your situation/location/system you will want to choose different
ways of installing PostgreSQL. A reliable way is to install the package provided
by your favorite linux distribution or use the normal Windows setup. If you are
on a foreign system and do not run the database on an analysis system (probably
because there is no separate system available for analysis) you might want to go
for the PostgreSQL portable package
<http://sourceforge.net/projects/postgresqlportable/>.

Depending on your installation you will need to create an username and password
but regardless of how you installed PostgreSQL you will need to create the
database manually as described in _PostgreSQL Setup_.

When run for the first time, DirStat 2 will automatically create tables and
indices as necessary. To clear all tables (but not delete the database itself)
the script `drop.sql` is provided for convenience. You can run it via

	$ psql masysma_dirstat
	masysma_dirstat=> \i drop.sql

You might need to enter the whole path to `drop.sql`. At that interactive shell
you can always perform queries ad-hoc without writing your own script and
without using the GUI. To find out which attributes and tables are defined,
extract `ma/dirstat/init.sql` from your DirStat 2 JAR file.

If you want to clear all traces of DirStat 2's database from your system, use
the described commands after the drop described above.

	# su postgres
	$ psql
	DROP DATABASE masysma_dirstat;
	DROP ROLE "linux-fan";
	\q
	exit

## PostgreSQL Setup

Follow <http://www.cyberciti.biz/faq/howto-add-postgresql-user-account/>
and <http://www.postgresql.org/docs/9.3/static/sql-createrole.html>

	# su postgres
	$ psql
	CREATE ROLE "linux-fan" WITH LOGIN PASSWORD 'testwort';
	CREATE DATABASE masysma_dirstat;
	GRANT ALL PRIVILEGES ON DATABASE masysma_dirstat TO "linux-fan";
	\q
	exit

On Windows systems, you can get the _SQL Shell (psql)_ from the GUI menu.

Also, you might chose arbitrary usernames and passwords to suit your needs but
the database name `masysma_dirstat` is fixed (unless you recompile DirStat 2).

## Scanning

If you intend to only perform one scan with standard settings you can enter

	$ java -jar dirstat2.jar --scan --db=user:password@host

This will scan all devices (on Windows) and the system root on Unix or Linux.
If you want to scan a specific directory you can alter the command this way:

	$ java -jar dirstat2.jar --scan --db=user:password@host --src=/etc

If you want to give the scan a name (the default value is of course `default`),
you can add a suitable parameter:

	$ java -jar dirstat2.jar --scan --db=user:password@host --name=linux1

Names must be unique for one database. The DB parameter contains the PostgreSQL
user, password and host (and optional port) which the server is running on.

While the scan is running, you will be presented status information, see
_Scan status_ for details. Further options for additional metadata to scan
are available via

	$ java -jar dirstat2.jar --help

If you want to disable TLS, you can prepend `INSECURE:` to your login data.
This is often necessary for Windows systems.

## Evaluation

To open the GUI for evaluation you can use the following command:

	$ java -jar dirstat2.jar --eval --db=user:password@host --name=linux1

This will open the scan named `linux1` in the GUI. Multiple GUI instances can
view the same dataset (there is a real database behind after all) and it is
possible to have another scan (with a different name) running while the GUI is
open. It is not recommended to view the data you are currently scanning because
it might be temporarily inconsistent (this is not a database issue but a design
choice of the scanning facility to maximize scanning performace).

The GUI should look like in the following pseudo-graphic

	/--[ DirStat 2 ]------------------------------------------[ _ [] X ]--\
	| Path          Pattern  Files  Size  Errors  Empty  Biggest etc.     |
	|.....................................................................|
	| linux1                                                              |
	|   Extensions  *.*      4096   10M   5       10     1M               |
	|     txt       *.txt    600     2M   0        2     64K              |
	|   FS Tree     /        4096   10M   5       10     1M               |
	|     /test     scanp    2048    5M   0        2     512K             |
	|     etc.                                                            |
	+----------------------+---------------------+------------------------+
	| General Information  | Empty files         | File Size Distribution |
	| .................... | ................... | ...................... |
	| . Key     . Value  . | . /test/empty.txt . | .                    . |
	| .................... | . /test/emty.txt  . | .          *         . |
	| . Objects . 5000   . | . /test/.empx.txt . | .   *      **        . |
	| . etc.    . etc.   . | . /test/.testrc   . | .  **     ****  *    . |
	| .................... | ................... | ...................... |
	\----------------------+----------------------------------------------/

As you can see it is divieded into four parts. The first and main part displays
the so-called _view_ which can be a file system structure or a view ordered
by file extension or such. The views are displayed at the top of the frame. The
three other parts are a table of general information, a list of empty files and
a diagram about the file size distribution whose x-Axis is logarithmic to allow
the user to view large filesizes (like 1\ TiB) and small file sizes (like
4\,KiB) in one diagram. The three lower parts always refer to the entry you have
selected in a view in the top panel.

To make advanced use of the views, you can attach a new view below an existing
one. It will not be displayed “below” in the tree structure but only contain
contents which occur below the node they were attached to. To view the file
system structure of all `.txt`-files you might want to select the entry _txt_
in the ``Extensions''-view and use the context menu to attach a new
_Default/tree.js_ view. The view will show you a subset of the scan in
hierarchical tree form (like in the real filesystem) but it will only contain
files and folders which are (or contain) `.txt`-files. Similarily, you can
attach a _Default/extension.js_ view below a node from the _FS Tree_ to
view the filetypes below a specific directory.

To get more advanced and special information about the scan, you can also
write your own views in JavaScript and attach them below other views or just add
them to the GUI by attaching them below the scan name (here: `linux1`). The
API for writing your own view is documented in the section _Writing your own
view_ below.

## Scan status

During the scan, DirStat 2 will print status lines about errors and the
scan progress. As there is only one scan pass, DirStat 2 does not know how far
it already is -- there is no “real” progress indicator. From the amount of
data scanned (which you can compare roughly to your HDD fill level if you are
scanning the whole HDD) you can get an estimate.

A normal status line will consist of key-value associations which contain
information about the number (or size) of items scanned and a change from the
last status display which is marked with a `+`-sign.

S  Symbol       Description
-  -----------  ---------------------------------------------
f  Files        The number of files scanned.
d  Directories  The number of directories completely scanned.
e  Errors       The number of errors occurred.
s  Size         Summarized size of all entities scanned.
c  Commited     The number of queries sent to the database.
q  Queries      The total number of queries ``queued''.

On fast filesystems it is normal that the scan has already completed and the
data has not yet been committed to the database. Scanning a normal HDD for the
first time it is likely that the filesystem scan time outweighs the database
commit time.

## Writing your own view

To write your own view, create a JavaScript file with the following skeleton.

~~~{.javascript}
function MyView() { }
MyView.prototype = new ViewJS();
var ref = MyView.prototype;

ref.root = null;

ref.create = function() {
	this.root = this.createNode(null);
	this.root.id = "Extension";
	this.root.pattern = "*.*";
}

ref.populateChildren = function(node) {
	// ...
	node.setReady();
}

ref.createFilter = function(node) {
	return this.createDefaultFilter("files.ext = ?",
						[ node.userdata ]);
}

function create_view() { return new MyView(); }
~~~

These functions have been copied from `extension.js`. Your own filter will of
course need a real name (although _MyView_ would also work) and an own
implementation of the methods. The Methods are defined as described below.

`create`
:   This function is invoked when your view is added. You should do basic
    initialization here. If your view will not dynamically add new nodes
    on demand but create them all at the beginning (like the Extension view
    does) you should do the node creation here. At least the root node
    should be created here.

`populateChildren(node)`
:   If your view dynamically creates the children on demand you implement
    this method which is invoked if the children should be created. The node
    parameter is either your root node or one of it's children. To mark that
    this node's children are now available call `node.setReady()`.

`createFilter(node)`
:   This method is invoked if another view is to be attached below your view
    or the secondary panels are to be updated for the node given as
    parameter. `default.js` has a convenicence function
    `createDefaultFilter` which allows you to write part of a prepared
    `WHERE` statement and give the parameters as a JavaScript array.
    This function returns the created filter as you can see in the skeleton.

`create_view`
:   This (not object oriented) function is invoked to create an object of
    your view. It should normally not do anything beyond creating the
    object as it might block the GUI. Do all initailization in `create`.

All views have to extend `ViewJS` which is implemented in `default.js`. To get
an overview over the methods available, you should extract `default.js` from
the DirStat 2 JAR file and scan it for useful methods. For further information
about implementing a view feel free to extract `extension.js` and `tree.js` from
the JAR -- they both contain working views and show you how to use the functions
you find in `default.js`.

## Querying the Database

Because of DirStat 2's filter concept, querying is rather difficult. Depending
on wether you want to query without a filter or using the filters the user of
the view might have imposed by attaching your view below another, you can use
two different methods.

`prepareQuery(query)`
:   This method creates a query which ignores all filters. It just returns
    a standard Java `PreparedStatement` with no filters enabled. You should
    only use it if you know that the data you query is unique for the _whole
    database_ (including possible other scans in the same database!).

`prepareSelect(query)`
:   This is the function you will more likely be using: It creates a query
    and attaches suitable filters before all `WHERE` clauses. If you do not
    have a `WHERE` clause yet, use `WHERE ?` to add a `WHERE` clause which
    only applies the filters. To populate such a `PreparedStatement` you
    need to invoke suitable `setString(...)` and similar methods to populate
    the `?`s you have created except for `WHERE ?` and interleave them with
    suitable calls to `appendFilterValues(query, pos)` to fill the implicit
    filters and `WHERE ?` clauses. Check `extension.js` and `tree.js` for
    examples.

Warning
:   As you will write a mixture of Java and JavaScript there can be a serious
    trouble with `String` objects: Sometimes they behave like JavaScript Strings
    and sometimes they behave like Java String objects. If you pass anything to
    a filter or Query like with `query.setString(pos++, value)` make sure it is
    a _Java_ String object and not a JavaScript String. Otherwise the
    application might just hang at 0% CPU usage for no visible reason. Similar
    issues occur when invoking nonexisting methods on objects.

## Known Issues

No error dialog
:   Error dialogs are a Tools 2.1 feature that has not yet been implemented.
    Therefore, DirStat 2 only logs errors to the console.
