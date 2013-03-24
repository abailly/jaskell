#!/bin/sh

# iterate over directories

while [ $# -gt 0 ] 
do
  i="$1"
  # compute list of classes
  flist=`find $i -name \*.class`
  # iterate over classes
  for f in $flist; do
      cname=`expr $f : '\(.*\).class'`
      echo -n "Verifying $cname ..."
      if java -verify -cp $i/..:../jaskell/bin $cname > /tmp/jout 2>&1 ; then
	  echo "OK"
      elif grep "java.lang.NoSuchMethodError: main" /tmp/jout > /dev/null 2>&1;  then
	  echo "OK"
      else
	  echo "KO"
          echo "======= Class $cname =======" >> /tmp/jerr
	  cat /tmp/jout >> /tmp/jerr
      fi
  done
  shift
done

if [ -f /tmp/jerr ]; then
  echo "Errors found, outputing verifier diagnostic :" 
  cat /tmp/jerr 
fi

# clenaup
[ -f /tmp/jout ] && rm /tmp/jout
[ -f /tmp/jerr ] && rm /tmp/jerr      
