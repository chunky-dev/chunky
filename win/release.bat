set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_31
set ANT_HOME=C:\Program Files\apache-ant-1.8.2
set PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;C:\Program Files (x86)\NSIS
echo PATH=%PATH%
cd ..
call ant release
cd win
call makensis Chunky.nsi
pause