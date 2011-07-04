SET JAR=..\build\EBookME.jar

java -Xmx512m -Duser.language=en -jar %JAR% "-Ddescription=The Old Testament of the King James Version of the Bible" "-Dname=The Old Testament" kjv1.txt "-Ddescription=The Old New of the King James Version of the Bible" "-Dname=The New Testament" kjv2.txt -Dout=bible -Dsplashimage=bibleCover.jpg

java -Xmx512m -Duser.language=cs -jar %JAR% -Dout=akazda -Dcharset=windows-1250 "-Ddescription=Povidka Alexandra Kazdy" "-Dname=Princ a drak" ak1.txt "-Dname=Zavod" ak2.txt "-Dname=Masky" ak3.txt "-Dname=Linda pise povidku" ak4.txt
