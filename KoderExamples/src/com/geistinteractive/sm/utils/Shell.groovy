com.geistinteractive.sm.utils
/*----[ above this line is ignored by FileMaker

/**
 * Execute Shell
 *
 * from ScriptMaster Examples -  360works.com
 *
 * @FileMakerFunctionName  util_Shell
 * @param command
 * @param waitForOutput
 * @param timeout
 *
 */


boolean isMac = System.getProperty("os.name").contains("Mac");
String[] cmds;
//if( isMac ) cmds = new String[] {"/bin/sh", "-c", command};
//else cmds = new String[] {"cmd.exe", "/C", command};
if( isMac ) cmds = ["/bin/sh", "-c", command];
else cmds = ["cmd.exe", "/C", command];

final Process process = Runtime.getRuntime().exec( cmds );
final InputStream inputStream = process.getInputStream();
final InputStream errorStream = process.getErrorStream();

final StringBuffer stdout = new StringBuffer();
final StringBuffer stderr = new StringBuffer();


if( Boolean.valueOf( waitForOutput ) ) { //Wait for command to finish

    Thread stdoutThread = new Thread( "stdout reader" ) {
        public void run() {
            try {
                Reader r = new InputStreamReader( inputStream, "utf-8" );
                char[] buff = new char[1024];
                int charsRead;
                while( ( charsRead = r.read( buff ) ) != -1 ) {
                    stdout.append( buff, 0, charsRead );
                }
            } catch( IOException e ) {
                throw new RuntimeException( e );
            }
        }
    };
    stdoutThread.start();

    Thread stderrThread = new Thread( "stderr reader" ) {
        public void run() {
            try {
                Reader r = new InputStreamReader( errorStream, "utf-8" );
                char[] buff = new char[1024];
                int charsRead;
                while( ( charsRead = r.read( buff ) ) != -1 ) {
                    stderr.append( buff, 0, charsRead );
                }
            } catch( IOException e ) {
                throw new RuntimeException( e );
            }
        }
    };
    stderrThread.start();

    final int timeoutMilliseconds = Integer.valueOf( timeout ) * 1000;
    if( timeoutMilliseconds > 0 ) {
        final Thread mainThread = Thread.currentThread();
        new Thread("timeout thread") {
            public void run() {
                try {
                    Thread.sleep( timeoutMilliseconds );
                    mainThread.interrupt();
                } catch( InterruptedException e ) {
                    //Ignore
                }
            }
        }.start();
    }
    try {
        if( process.waitFor() == 0 ) { //Successful
            stdoutThread.join(); //Wait for all output to be read
            return stdout.toString();
        } else { //Error when running command
            stderrThread.join(); //Wait for entire error message to be read
            throw new RuntimeException( stderr.toString() );
        }
    } catch( InterruptedException e ) {
        throw new RuntimeException("Process was interrupted; error output is: " + stderr.toString() );
    }
} else { //Don't wait, return immediately
    inputStream.close();
    errorStream.close();
    return "Executed shell command: " + command;
}