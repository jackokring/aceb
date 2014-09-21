# aceb

Android only continuation of http://acebforth.googlecode.com on Google.
The other platforms will no longer be developed for. Strictly Android only.

The FORTH system supported is very close to the Jupiter Ace's FORTH, with some
removed words based on them having little use for a 16 bit address width 
FORTH, and some additions for modern utility. It is not and never will be an
ANS FORTH. There are many differences.

Extensions use an Intent interface, so that anybody may make an Android
application which supports the possible event triggers. This allows a great
flexibility. This app itself supports some triggers to allow other
apps to use this one as a language and evaluation service.

* .ace - a text FORTH file extension (for code to compile)
* .aceb - a binary format file extension (for snapshots of memory)

Check the Wiki for more information. I've included the extra project which
eclipse made called appcompat_v7 for handling the action bar. Apart from that
this project is an exact dump of the working project directory, including all
setup files.

For licence information excluding the appcompat_v7 see [[LICENCE.md]] which is
quite open source if you want it.
