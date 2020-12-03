/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.co.antenna.XfoJavaCtl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * XfoObj Class is the object class of XSL Formatter
 *
 * @author Test User
 */
public class XfoObj {

    public Process process = null;
    public volatile boolean processValid = true;
    public int formatterMajorVersion = 0;
    public int formatterMinorVersion = 0;
    public String formatterRevision = "";
    //FIXME also get build number string?

    // hack to use STDOUT instead of -o option
    //
    // Java has a bug/limitation in Windows where files are not opened with
    // FILE_SHARE_DELETE and will prevent Formatter from deleting the output
    // file specified with '-o' if an error occurs.  Formatter will then
    // report the fatal error as 'couldn't delete ouptut' file instead of the
    // real cause.  Ahrts needs to keep a copy of the output file open in
    // order to monitor the output progress.
    //
    // Note that with the use of STDOUT the final output file will only be
    // written when formatting is complete.  It will not be possible to read
    // the output as it's being written in order to create progress updates.
    public boolean useStdoutFix = false;

    private String outputFilename = "";

    // Consts
    public static final int EST_NONE = 0;
    public static final int EST_STDOUT = 1;
    public static final int EST_STDERR = 2;
    private static final String[] AH_HOME_ENV = {
        "AHF70_64_HOME", "AHF70_HOME",
        "AHF66_64_HOME", "AHF66_HOME",
        "AHF65_64_HOME", "AHF65_HOME",
        "AHF64_64_HOME", "AHF64_HOME",
        "AHF63_64_HOME", "AHF63_HOME",
        "AHF62_64_HOME", "AHF62_HOME",
        "AHF61_64_HOME", "AHF61_HOME",
        "AHF60_64_HOME", "AHF60_HOME",
        "AHF53_64_HOME", "AHF53_HOME",
        "AHF52_64_HOME", "AHF52_HOME",
        "AHF51_64_HOME", "AHF51_HOME",
        "AHF50_64_HOME", "AHF50_HOME",
        "AXF43_64_HOME", "AXF43_HOME",
        "AXF42_HOME",
        "AXF41_HOME",
        "AXF4_HOME"};

    private static final HashMap<String, Integer> AH_VER_MAP;
    static {
        Integer zero = new Integer(0);
        AH_VER_MAP = new HashMap<String, Integer>();
        AH_VER_MAP.put("AXF43_64_HOME", zero);
        AH_VER_MAP.put("AXF43_HOME", zero);
        AH_VER_MAP.put("AXF42_HOME", zero);
        AH_VER_MAP.put("AXF41_HOME", zero);
        AH_VER_MAP.put("AXF4_HOME", zero);
    }

    public static final int S_PDF_EMBALLFONT_PART = 0;
    public static final int S_PDF_EMBALLFONT_ALL = 1;
    public static final int S_PDF_EMBALLFONT_BASE14 = 2;

    public static final int S_FORMATTERTYPE_AUTO = 0;
    public static final int S_FORMATTERTYPE_HTML = 1;
    public static final int S_FORMATTERTYPE_XHTML = 2;
    public static final int S_FORMATTERTYPE_XMLCSS = 3;
    public static final int S_FORMATTERTYPE_XSLFO = 4;

    public static final int S_PDF_VERSION_13 = 0;
    public static final int S_PDF_VERSION_14 = 1;
    public static final int S_PDF_VERSION_15 = 2;
    public static final int S_PDF_VERSION_16 = 3;
    public static final int S_PDF_VERSION_17 = 4;
    public static final int S_PDF_VERSION_20 = 10;

    public static final int S_PDF_VERSION_A_1a_2005 = 200;
    public static final int S_PDF_VERSION_A_1b_2005 = 400;

    public static final int S_PDF_VERSION_A_2a_2011_14 = 631;
    public static final int S_PDF_VERSION_A_2a_2011_15 = 632;
    public static final int S_PDF_VERSION_A_2a_2011_16 = 633;
    public static final int S_PDF_VERSION_A_2a_2011_17 = 634;

    public static final int S_PDF_VERSION_A_2b_2011_14 = 641;
    public static final int S_PDF_VERSION_A_2b_2011_15 = 642;
    public static final int S_PDF_VERSION_A_2b_2011_16 = 643;
    public static final int S_PDF_VERSION_A_2b_2011_17 = 644;

    public static final int S_PDF_VERSION_A_2u_2011_14 = 651;
    public static final int S_PDF_VERSION_A_2u_2011_15 = 652;
    public static final int S_PDF_VERSION_A_2u_2011_16 = 653;
    public static final int S_PDF_VERSION_A_2u_2011_17 = 655;

    public static final int S_PDF_VERSION_A_3a_2012_14 = 661;
    public static final int S_PDF_VERSION_A_3a_2012_15 = 662;
    public static final int S_PDF_VERSION_A_3a_2012_16 = 663;
    public static final int S_PDF_VERSION_A_3a_2012_17 = 664;

    public static final int S_PDF_VERSION_A_3b_2012_14 = 671;
    public static final int S_PDF_VERSION_A_3b_2012_15 = 672;
    public static final int S_PDF_VERSION_A_3b_2012_16 = 673;
    public static final int S_PDF_VERSION_A_3b_2012_17 = 674;

    public static final int S_PDF_VERSION_A_3u_2012_14 = 681;
    public static final int S_PDF_VERSION_A_3u_2012_15 = 682;
    public static final int S_PDF_VERSION_A_3u_2012_16 = 683;
    public static final int S_PDF_VERSION_A_3u_2012_17 = 684;

    public static final int S_PDF_VERSION_UA1_15 = 1002;
    public static final int S_PDF_VERSION_UA1_16 = 1003;
    public static final int S_PDF_VERSION_UA1_17 = 1004;

    public static final int S_PDF_VERSION_X_1a_2001 = 101;
    public static final int S_PDF_VERSION_X_1a_2003 = 104;
    public static final int S_PDF_VERSION_X_2_2003 = 105;
    public static final int S_PDF_VERSION_X_3_2002 = 103;
    public static final int S_PDF_VERSION_X_3_2003 = 106;

    // changed to S_PDF_VERSION_X_4_2010, keep for compatibility
    public static final int S_PDF_VERSION_X_4_2008 = 107;
    public static final int S_PDF_VERSION_X_4_2010 = 107;

    public static final int S_PDF_VERSION_X_4p_2010 = 108;

    // Attributes
    private String preferredHome;
    private String specifiedFormatterInstallation;
    private String executable;
    private Runtime r;
    private MessageListener messageListener;
    private LinkedHashMap<String, String> args;
    private ArrayList<String> userCSS;
    private XfoException lastError;
    private String os;
    private boolean isWindows;
    private String axf_home = null;
    private File workingDir;

    private String envStart;
    private ArrayList<String> envp = new ArrayList<String>();

    // Methods

    private ProcessBuilder initProcessBuilder(String[] cmdParams, String[] envParams) {

        ProcessBuilder pb = new ProcessBuilder(cmdParams);

        // https://github.com/AntennaHouse/Alternate-XfoJavaCtl/issues/8
        if (this.workingDir != null) {
            pb.directory(workingDir);
        }

        Map<String, String> env = pb.environment();

        if (this.preferredHome != null) {
            if (this.isWindows) {
                String path = env.get("Path");

                if (path == null) {
                    path = "";
                }
                env.put("Path", this.axf_home + ";" + path);
            } else if (os.equals("Mac OS X")) {
                String ldpath = env.get("DYLD_LIBRARY_PATH");

                if (ldpath == null) {
                    ldpath = "";
                }
                env.put("DYLD_LIBRARY_PATH", axf_home + "/lib:" + ldpath);
            } else {
                String ldpath = env.get("LD_LIBRARY_PATH");

                if (ldpath == null) {
                    ldpath = "";
                }
                env.put("LD_LIBRARY_PATH", axf_home + "/lib:" + ldpath);
            }
        }

        if(envParams.length > 0) {
            for(String envParam : envParams) {
                String[] keyVal = envParam.split("=");
                env.put(keyVal[0].trim(), keyVal[1].trim());
            }
        }

        return pb;
    }

    /* assumes keys are of type AHFxxx_HOME where 'x's are integers */
    private int parseFormatterVersionFromKey (String key) {
        String sub = key.substring(3, key.length() - "_HOME".length());
        if (sub.endsWith("_64")) {
            sub = sub.substring(0, sub.length() - "_64".length());
        }

        int version = 0;
        try {
            version = Integer.parseInt(sub);
        } catch (NumberFormatException e) {
            // pass, just return 0
        }

        return version;
    }

    private File checkForLatestUnixFormatterDirectory (String os, String checkBaseDir) {
        // only used with unix versions
        if (os.contains("Windows")) {
            return null;
        }

        String baseDir = "";
        String dirNamePattern = "";

        if (os.contains("Mac OS X")) {
            // /usr/local/AHFormatterV62

            baseDir = "/usr/local";
            dirNamePattern = "AHFormatterV";
        } else if (os.contains("SunOS")) {
            // /opt/ANTHahfv62

            baseDir = "/opt";
            dirNamePattern = "ANTHahfv";
        } else {  // default to Linux
            // /usr/AHFormatterV62     32 bit
            // /usr/AHFormatterV62_64  64 bit

            baseDir = "/usr";
            dirNamePattern = "AHFormatterV";
        }

        if (checkBaseDir != null) {
            File f;

            f = new File(checkBaseDir);
            baseDir = f.getParentFile().getAbsolutePath();
        }

        File dir = new File(baseDir);
        File[] files = dir.listFiles();

        File foundDir = null;
        boolean foundDirIs64Bit = false;
        int foundVersion = 0;

        for (File f : files) {
            String name = f.getName();
            boolean bit64 = false;

            //System.out.println("file: " + f.getName());
            if (name.startsWith(dirNamePattern)) {
            // stop here and do next iteration if current AHF dir
            // is not equal with the provided checkBaseDir
            if(checkBaseDir != null && !f.getAbsolutePath().equals(checkBaseDir)) {
                continue;
            }

            String versionString;
            int version = 0;

        //System.out.println("checking...");
        if (name.endsWith("_64")) {
            bit64 = true;
            versionString = name.substring(dirNamePattern.length(), name.length() - 3);
        } else {
            versionString = name.substring(dirNamePattern.length());
        }

        //System.out.println("version string: " + versionString);
        try {
            version = Integer.parseInt(versionString);
        } catch (NumberFormatException e) {
            // pass, leave at zero
        }

        if (version > foundVersion) {
            foundVersion = version;
            foundDir = f;
            foundDirIs64Bit = bit64;
        } else if (version == foundVersion  &&  bit64) {
            // preference for 64 bit versions
            foundDir = f;
            foundDirIs64Bit = bit64;
        }
        }
    }  // end f : fileList

    //FIXME not here?
    if (foundDir != null) {
        // also setup environment variables

        //FIXME clone environment and add?

        String absPath = foundDir.getAbsolutePath();

        String ldLibPath = "LD_LIBRARY_PATH=" + absPath + "/lib";
        Map<String, String> osEnv = System.getenv();
        if (osEnv.containsKey("LD_LIBRARY_PATH")) {
        ldLibPath += ":" + osEnv.get("LD_LIBRARY_PATH");
        }
        envp.add(ldLibPath);

        // for mac os x
        ldLibPath = "DYLD_LIBRARY_PATH=" + absPath + "/lib";
        if (osEnv.containsKey("DYLD_LIBRARY_PATH")) {
        ldLibPath += ":" + osEnv.get("DYLD_LIBRARY_PATH");
        }
        envp.add(ldLibPath);

        envStart = "AHF" + foundVersion;

        // Mac OS X versions since 6.4 are 64-bit.  The directory is still
        // named like the 32-bit version (missing '_64', ex:
        // /usr/local/AHFormater64) but the environment variables use the
        // '_64' extension (ex:  AHF64_64_HOME).
        if (foundDirIs64Bit  ||  (os.contains("Mac OS X") &&  foundVersion >= 64)) {
        envStart += "_64";
        }

        envp.add(envStart + "_HOME" + "=" + absPath);
        envp.add(envStart + "_LIC_PATH" + "=" + absPath + "/etc");
        envp.add(envStart + "_HYPDIC_PATH" + "=" + absPath + "/etc/hyphenation");
        // DMC_TBLPATH removed since version 7, harmless to add
        envp.add(envStart + "_DMC_TBLPATH" + "=" + absPath + "/sdata/base2");
        envp.add(envStart + "_DEFAULT_HTML_CSS" + "=" + absPath + "/etc/html.css");
        envp.add(envStart + "_FONT_CONFIGFILE" + "=" + absPath + "/etc/font-config.xml");
        envp.add(envStart + "_BROKENIMG" + "=" + absPath + "/samples/Broken.png");

        /*
        for (String s : envp) {
        System.out.println("env: " + s);
        }
        */
    }

    return foundDir;
    }

    private void setCustomFormatterLocationEnvironmentVariables () throws XfoException {
    //FIXME Windows

    String absPath = specifiedFormatterInstallation;
    Map<String, String> osEnv = System.getenv();

    if (isWindows) {
        String path = "Path=" + absPath;
        if (osEnv.containsKey("Path")) {
        path += ";" + osEnv.get("Path");  //FIXME File.pathSeparator
        }
        envp.add(path);
    } else {
        String ldLibPath = "LD_LIBRARY_PATH=" + absPath + "/lib";

        if (osEnv.containsKey("LD_LIBRARY_PATH")) {
        ldLibPath += ":" + osEnv.get("LD_LIBRARY_PATH");
        }
        envp.add(ldLibPath);

        // for mac os x
        ldLibPath = "DYLD_LIBRARY_PATH=" + absPath + "/lib";
        if (osEnv.containsKey("DYLD_LIBRARY_PATH")) {
        ldLibPath += ":" + osEnv.get("DYLD_LIBRARY_PATH");
        }
        envp.add(ldLibPath);
    }

    // needs to be *_HOME

    if (preferredHome == null) {
        //FIXME Windows
        File f = checkForLatestUnixFormatterDirectory(os, specifiedFormatterInstallation);
        if (f == null) {
        throw new XfoException(4, 0, "Formatter version couldn't be determined from specified installation");
        }

        // environment variables set in checkFor...() call
        return;
    }

    if (preferredHome.length() < 6) {
        throw new XfoException(4, 0, "invalid 'preferredHome' specified");
    }

    String etcDir = File.separator + "etc";
    if (isWindows) {
        etcDir = "";
    }

    envStart = preferredHome.substring(0, preferredHome.length() - 5);

    envp.add(envStart + "_HOME" + "=" + absPath);
    envp.add(envStart + "_LIC_PATH" + "=" + absPath + etcDir);
    envp.add(envStart + "_HYPDIC_PATH" + "=" + absPath + etcDir + File.separator + "hyphenation");
    // DMC_TBLPATH removed since version 7, harmless to add
    envp.add(envStart + "_DMC_TBLPATH" + "=" + absPath + File.separator + "sdata" + File.separator + "base2");
    envp.add(envStart + "_DEFAULT_HTML_CSS" + "=" + absPath + etcDir + File.separator + "html.css");
    envp.add(envStart + "_FONT_CONFIGFILE" + "=" + absPath + etcDir + File.separator + "font-config.xml");
    envp.add(envStart + "_BROKENIMG" + "=" + absPath + File.separator + "samples" + File.separator + "Broken.png");
    }

    /**
     * Create the instance of XfoObj, and initialize it.
     *
     * @param preferredHome  Use a specific version of Formatter.  This is
     * specified as a Formatter home environment variable (ex: AHF63_HOME,
     * AHF62_64_HOME, etc.) and needs to already be defined in the user's
     * environment.  In addition to using the executable found with this
     * variable the Formatter process that is launched alters the PATH and
     * [DY]LD_LIBRARY_PATH settings to match the specified environment.
     *
     * @param specifiedFormatterInstallation  Location of base directory of the
     * Formatter installation.  This should be specified as an absolute path.
     * 'preferredHome' is used to set the appropriate environment variables
     * for that version.
     *
     * If 'preferredHome' is set to null the Unix versions will try to parse
     * the Formatter version from the directory specified with
     * 'specifiedFormatterInstallation'.  The name of the directory should be the
     * same as the name used for default Formatter installations.  For
     * example, the 32-bit Linux version installs into /usr/AHFormatterV63.
     * A valid 'specifiedFormatterInstallation' directory would be
     * /share/data/AHFormatterV63.
     *
     * @throws XfoException
     */
    public XfoObj (String preferredHome, String specifiedFormatterInstallation) throws XfoException {
        // Check EVs and test if XslCmd.exe exists.
    this.preferredHome = preferredHome;
    this.specifiedFormatterInstallation = specifiedFormatterInstallation;
    try {
        os = System.getProperty("os.name");
        if ((os == null) || os.equals("")) {
            throw new Exception();
        }
        isWindows = os.contains("Windows");
    } catch (Exception e) {
        throw new XfoException(4, 0, "Could not determine OS");
    }

    // ahrts
    axf_home = System.getProperty("axf.home");
    int axf_ver = 1;

    Map<String, String> env = System.getenv();

    if ((axf_home == null) || axf_home.equals("")) {
        try {
        if (preferredHome != null  &&  specifiedFormatterInstallation == null) {
            if (env.containsKey(preferredHome)) {
            axf_home = env.get(preferredHome);
            }
            // else fall back to other checks
        } else if (specifiedFormatterInstallation != null) {
            //System.out.println("specified: " + specifiedFormatterInstallation);
            axf_home = specifiedFormatterInstallation;
            setCustomFormatterLocationEnvironmentVariables();
        }

        if (axf_home == null  ||  axf_home.equals("")) {
            for (String key: AH_HOME_ENV) {
            if (env.containsKey(key)) {
                axf_home = env.get(key);
                if (AH_VER_MAP.containsKey(key)) {
                axf_ver = AH_VER_MAP.get(key).intValue();
                }
                break;
            }
            }
        }

        // check possible future versions of Formatter
        if (axf_home == null  ||  axf_home.equals("")) {
            String foundKey = "";
            int foundVersion = 0;

            //List<String> foundKeys = new List<String>();

            for (String key: env.keySet()) {
            String ukey;
            if (isWindows) {
                ukey = key.toUpperCase();
            } else {
                ukey = key;
            }

            if (ukey.startsWith("AHF")  &&  ukey.endsWith("_HOME")) {
                int version = parseFormatterVersionFromKey(ukey);
                if (version > foundVersion) {
                foundVersion = version;
                foundKey = ukey;
                }
            }
            } // end for (String key: ...

            if (!foundKey.equals("")) {
            axf_home = env.get(foundKey);
            }
        }

        // check some default unix paths
        if ((axf_home == null) || axf_home.equals("")) {
            File foundDir = checkForLatestUnixFormatterDirectory(os, null);
            if (foundDir != null) {
            axf_home = foundDir.getAbsolutePath();
            }
        }

        if ((axf_home == null) || axf_home.equals(""))
            throw new Exception("axf home is unset");
        } catch (XfoException e) {
        throw e;
        } catch (Exception e) {
        throw new XfoException(4, 1, "Could not locate Formatter's environment variables");
        }
    } else {  // axf.home was specified
        // ahrts
        String axf_version = System.getProperty("axf.version");
        if (axf_version != null  &&  !axf_home.equals("")) {
        this.specifiedFormatterInstallation = axf_home;
        this.preferredHome = axf_version;
        setCustomFormatterLocationEnvironmentVariables();
        }
    }

    String separator = System.getProperty("file.separator");
    this.executable = axf_home + separator;
    if (System.getProperty("axf.bin") == null) {
        if (os.equals("Linux") || os.equals("SunOS") || os.equals("AIX") || os.equals("Mac OS X")) {
        if (axf_ver == 0) {
            this.executable += "bin" + separator + "XSLCmd";
        } else {
            this.executable += "bin" + separator + "AHFCmd";
        }
        }
        else if (os.contains("Windows")) {
        if (axf_ver == 0)
            this.executable += "XSLCmd.exe";
        else
            this.executable += "AHFCmd.exe";
        }
        else
        throw new XfoException(4, 2, "Unsupported OS: " + os);
    }
    else {
        this.executable += System.getProperty("axf.bin");
    }
        // setup attributes
        this.clear();
    }

    public XfoObj (String preferredHome) throws XfoException {
        this(preferredHome, null);
    }

    public XfoObj () throws XfoException {
        this(null, null);
    }

    /**
     * Cleanup (initialize) XSL Formatter engine.
     */
    public void clear () {
        // reset attributes
        this.r = Runtime.getRuntime();
        this.args = new LinkedHashMap<String, String>();
        this.messageListener = null;
        this.userCSS = new ArrayList<String>();
        this.lastError = null;
        this.process = null;
        this.processValid = true;
        this.outputFilename = "";
        this.workingDir = null;
    }

    //FIXME removing since '-v' option isn't a good test to see if Formatter
    // is working correctly since it will exit with code 1 in older versions.

    /*
    public void test () throws XfoException, InterruptedException {
    versionCheck(true);
    try {
        execute();
    } catch (XfoException e) {
        // older versions of Formatter (<= 6.0) will have a '1' exit code
        // when version is printed
        if (e.getErrorCode() == 0  &&  formatterMajorVersion > 0) {
        // pass
        } else {
        throw e;
        }
    } finally {
        versionCheck(false);
    }
    }
    */

    /**
     * Execute formatting and outputs to a PDF.
     *
     * @throws jp.co.antenna.XfoJavaCtl.XfoException
     */
    public void execute () throws XfoException, InterruptedException {
        ArrayList<String> cmdArray = new ArrayList<String>();
        cmdArray.add(this.executable);
        for (String arg : this.args.keySet()) {
            cmdArray.add(arg);
            //FIXME check if Formatter >= 6.1mr2 which implements stdout corruption fix
            if (useStdoutFix  &&  arg.equals("-o")) {
                cmdArray.add("@STDOUT");
            } else if (this.args.get(arg) != null) {
                cmdArray.add(this.args.get(arg));
            }
        }

        for (String css : this.userCSS) {
            cmdArray.add("-css");
            cmdArray.add(css);
        }

        // Run Formatter with Runtime.exec()
        //Process process;
        ProcessBuilder pb;
        ErrorParser errorParser = null;
        StreamFlusher outputFlush = null;
        StreamCopyThread outputFileFlush = null;
        int exitCode = -1;
        try {
            String[] s = new String[0];
        try {
            if (envp.size() > 0) {
                // environment variables have already been set by using 'specifiedFormatterInstallation' or finding default unix Formatter installation
                String[] e = new String[envp.size()];
                envp.toArray(e);

                pb = initProcessBuilder(cmdArray.toArray(s), e);

            } else {

                pb = initProcessBuilder(cmdArray.toArray(s), new String[0]);
            }

            process = pb.start();

        } catch (IOException ioex) {
            processValid = false;
            String msg = "couldn't invoke axfo: " + ioex.getMessage();
            System.err.println(msg);
            throw new XfoException(4, 0, msg);
        }
        try {
            InputStream StdErr = process.getErrorStream();
            InputStream StdOut = process.getInputStream();
            errorParser = new ErrorParser(StdErr, this.messageListener);
            errorParser.start();
            if (useStdoutFix) {
                outputFileFlush = new StreamCopyThread(StdOut, new FileOutputStream(new File(outputFilename)));
                outputFileFlush.start();
            } else {
                outputFlush = new StreamFlusher(StdOut);
                outputFlush.start();
            }
        } catch (Exception e) {
            String msg = "Exception getting streams: " + e.getMessage();
            System.err.println(msg);
            throw new XfoException(4, 0, msg);
        }
        try {
            exitCode = process.waitFor();
            process = null;
        } catch (InterruptedException e) {
            String msg = "InterruptedException waiting for axfo to finish: " + e.getMessage();
            System.err.println(msg);
            //FIXME ahfcmd is still running though...
            if (process != null) {
                process.destroy();
                process.waitFor();
                process = null;
                if (outputFlush != null) {
                    outputFlush.interrupt();
                    outputFlush.join();
                //System.out.println("interrupting flush thread");
            }
            if (outputFileFlush != null) {
                outputFileFlush.interrupt();
                outputFileFlush.join();
            }
            if (errorParser != null) {
                errorParser.interrupt();
                errorParser.join();
                //System.out.println("interrupting error parsing thread");
            }
            //System.out.println("killed ahfcmd process");
        }
        Thread.interrupted();
        //throw new XfoException(4, 0, msg);
        throw new InterruptedException();
        }
    } catch (XfoException e) {
        // maybe set process = null here as well
        throw e;
        /*
        } catch (Exception e) {
        String msg = "Exception waiting for axfo to finish: " + e.getMessage();
        System.err.println(msg);
        throw new XfoException(4, 0, msg);
        */
    }

    if (outputFileFlush != null) {
        try {
        outputFileFlush.join();
        } catch (InterruptedException e) {
        String msg = "Exception output file flush: " + e.getMessage();
        System.err.println(msg);
        Thread.interrupted();
        throw new InterruptedException();
        //throw new XfoException(4, 0, msg);
        }
    }

    if (outputFlush != null) {
        try {
        outputFlush.join();
        } catch (InterruptedException e) {
        String msg = "Exception output flush: " + e.getMessage();
        System.err.println(msg);
        Thread.interrupted();
        throw new InterruptedException();
        //throw new XfoException(4, 0, msg);
        }
    }

    if (errorParser != null) {
        try {
        errorParser.join();
        formatterMajorVersion = errorParser.majorVersion;
        formatterMinorVersion = errorParser.minorVersion;
        formatterRevision = errorParser.revision;
        } catch (InterruptedException e) {
        String msg = "Exception joining error parser: " + e.getMessage();
        System.err.println(msg);
        Thread.interrupted();
        //throw new XfoException(4, 0, msg);
        throw new InterruptedException();
        }
    }

        if (exitCode != 0) {
            if (errorParser != null && errorParser.LastErrorCode != 0) {
                this.lastError = new XfoException(errorParser.LastErrorLevel, errorParser.LastErrorCode, errorParser.LastErrorMessage);
        throw this.lastError;
            } else {
                throw new XfoException(4, 0, "Axfo Exit code: " + exitCode + " last message: " + errorParser.UnknownErrorMessage);
            }
        }
    }

    public int getErrorCode () throws XfoException {
    if (this.lastError == null)
        return 0;
    else
        return this.lastError.getErrorCode();
    }

    public int getErrorLevel () throws XfoException {
    if (this.lastError == null)
        return 0;
    else
        return this.lastError.getErrorLevel();
    }

    public String getErrorMessage () throws XfoException {
    if (this.lastError == null)
        return null;
    else
        return this.lastError.getErrorMessage();
    }

    public int getExitLevel () throws XfoException {
        String opt = "-extlevel";
        if (this.args.containsKey(opt))
            return Integer.parseInt(this.args.get(opt));
        return 2;  // default exit level
    }

    public void releaseObject () {
    this.clear();
    }

    public void releaseObjectEx () throws XfoException {
        releaseObject();
    }

    /**
     * Executes the formatting of XSL-FO document specified for src, and outputs it to dst in the output form specified for dst.
     *
     * @param src   XSL-FO Document
     * @param dst   output stream
     * @param outDevice output device. Please refer to a setPrinterName method about the character string to specify.
     * @throws jp.co.antenna.XfoJavaCtl.XfoException
     */
    public void render (InputStream src, OutputStream dst, String outDevice) throws XfoException, InterruptedException {
    ArrayList<String> cmdArray = new ArrayList<String>();
    cmdArray.add(this.executable);
    for (String arg : this.args.keySet()) {
        cmdArray.add(arg);
        if (this.args.get(arg) != null)
        cmdArray.add(this.args.get(arg));
    }
    for (String css : this.userCSS) {
        cmdArray.add("-css");
        cmdArray.add(css);
    }
    cmdArray.add("-d");
    cmdArray.add("@STDIN");
    cmdArray.add("-o");
    cmdArray.add("@STDOUT");
    cmdArray.add("-p");
    if (outDevice != null  &&  outDevice.length() != 0) {
        cmdArray.add(outDevice);
    } else {
        cmdArray.add("@PDF");
    }
    cmdArray.add(outDevice);

    //Process process;
    ProcessBuilder pb;
    ErrorParser errorParser = null;
    StreamCopyThread scInput = null;
    StreamCopyThread scOutput = null;

    int exitCode = -1;

    try {
        String[] s = new String[0];
        try {
        if (envp.size() > 0) {
            // environment variables have already been set by using 'specifiedFormatterInstallation' or finding default unix Formatter installation
            String[] e = new String[envp.size()];
            envp.toArray(e);
            //FIXME processbuilder

            // https://github.com/AntennaHouse/Alternate-XfoJavaCtl/issues/8
            if (this.workingDir != null) {
            process = this.r.exec(cmdArray.toArray(s), e, this.workingDir);
            } else {
            process = this.r.exec(cmdArray.toArray(s), e);
            }
        } else {
            //process = this.r.exec(cmdArray.toArray(s));
            pb = new ProcessBuilder(cmdArray.toArray(s));
            // https://github.com/AntennaHouse/Alternate-XfoJavaCtl/issues/8
            if (this.workingDir != null) {
            pb.directory(workingDir);
            }
            Map<String, String> env = pb.environment();

            if (preferredHome != null) {
            if (isWindows) {
                String path = env.get("Path");

                if (path == null) {
                path = "";
                }
                env.put("Path", axf_home + ";" + path);
            } else if (os.equals("Mac OS X")) {
                String ldpath = env.get("DYLD_LIBRARY_PATH");

                if (ldpath == null) {
                ldpath = "";
                }
                env.put("DYLD_LIBRARY_PATH", axf_home + "/lib:" + ldpath);
            } else {
                String ldpath = env.get("LD_LIBRARY_PATH");

                if (ldpath == null) {
                ldpath = "";
                }
                env.put("LD_LIBRARY_PATH", axf_home + "/lib:" + ldpath);
            }
            }

            process = pb.start();
        }
        } catch (IOException ioex) {
        String msg = "render() couldn't invoke axfo: " + ioex.getMessage();
        System.err.println(msg);
        throw new XfoException(4, 0, msg);
        }
        try {
        InputStream StdErr = process.getErrorStream();
        errorParser = new ErrorParser(StdErr, this.messageListener);
        errorParser.start();

        scInput = new StreamCopyThread(process.getInputStream(), dst);
        scInput.start();
        scOutput = new StreamCopyThread(src, process.getOutputStream());
        scOutput.start();
        } catch (Exception e) {
        String msg = "Exception creating threads in render(): " + e.getMessage();
        System.err.println(msg);
        //e.printStackTrace();
        throw new XfoException(4, 0, msg);
        }

        exitCode = process.waitFor();
    } catch (Exception e) {
        String msg = "Exception in render(): " + e.getMessage();
        System.err.println(msg);
        //e.printStackTrace();
        throw new XfoException(4, 0, msg);
    }

    if (scInput != null) {
        try {
        scInput.join();
        } catch (InterruptedException e) {
        String msg = "Exception joining render input: " + e.getMessage();
        System.err.println(msg);
        Thread.interrupted();
        throw new InterruptedException();
        //throw new XfoException(4, 0, msg);
        }
    }

    if (scOutput != null) {
        try {
        scOutput.join();
        } catch (InterruptedException e) {
        String msg = "Exception joining render output: " + e.getMessage();
        System.err.println(msg);
        Thread.interrupted();
        throw new InterruptedException();
        //throw new XfoException(4, 0, msg);
        }
    }

    if (errorParser != null) {
        try {
        errorParser.join();
        formatterMajorVersion = errorParser.majorVersion;
        formatterMinorVersion = errorParser.minorVersion;
        formatterRevision = errorParser.revision;
        } catch (InterruptedException e) {
        String msg = "Exception joining error parser: " + e.getMessage();
        System.err.println(msg);
        Thread.interrupted();
        throw new InterruptedException();
        //throw new XfoException(4, 0, msg);
        }
    }

    if (exitCode != 0) {
        if (errorParser != null && errorParser.LastErrorCode != 0) {
        this.lastError = new XfoException(errorParser.LastErrorLevel, errorParser.LastErrorCode, errorParser.LastErrorMessage);
        throw this.lastError;
        } else {
        throw new XfoException(4, 0, "Exit code: " + exitCode + " render 2 last message: " + errorParser.UnknownErrorMessage);
        }
    }
    }

    /**
     * Transforms an XML document specified to xmlSrc using an XSL stylesheet specified to xslSrc.
     * Then executes the formatting of XSL-FO document and outputs it to dst in the output form specified for outDevice.
     * Xalan of JAXP (Java API for XML Processing) is used for the XSLT conversion. The setExternalXSLT method and
     * the setting of XSLT processor in the option setting file is disregarded.
     *
     * @param xmlSrc XML Document
     * @param xslSrc XSL Document
     * @param dst output stream
     * @param outDevice output device. Please refer to a setPrinterName method about the character string to specify.
     * @throws jp.co.antenna.XfoJavaCtl.XfoException
     */
    public void render(InputStream xmlSrc, InputStream xslSrc, OutputStream dst, String outDevice) throws XfoException {
    try {
        ByteArrayOutputStream baosFO = new ByteArrayOutputStream();
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer(new StreamSource(xslSrc));
        transformer.transform(new StreamSource(xmlSrc), new StreamResult(baosFO));
        ByteArrayInputStream baisFO = new ByteArrayInputStream(baosFO.toByteArray());
        this.render(baisFO, dst, outDevice);
    } catch (XfoException xfoe) {
        throw xfoe;
    } catch (Exception e) {
        throw new XfoException(4, 0, "XSLT Transformation failed: " + e.toString() + " render 2");
    }
    }

    private void versionCheck (boolean check) {
        String opt = "-v";
        if (check) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    /**
     * Sets the default base URI.
     *
     * @param uri Base URI
     * @throws jp.co.antenna.XfoJavaCtl.XfoException
     */
    public void setBaseURI (String uri) {
    String opt = "-base";
    if (uri != null && !uri.equals("")) {
        if (this.args.containsKey(opt))
        this.args.remove(opt);
        this.args.put(opt, uri);
    } else {
        this.args.remove(opt);
    }
    }

    public void setBatchPrint (boolean bat) {
        // Fake it.
    }

    /**
     * Set the URI of XML document to be formatted.
     * <br>If specified "@STDIN", XML document reads from stdin. The document that is read from stdin is assumed to be FO.
     *
     * @param uri URI of XML document
     * @throws jp.co.antenna.XfoJavaCtl.XfoException
     */
    public void setDocumentURI (String uri) throws XfoException {
        // Set the URI...
        String opt = "-d";
        if (uri != null && !uri.equals("")) {
            if (this.args.containsKey(opt))
                this.args.remove(opt);
            this.args.put(opt, uri);
        }
        else {
            this.args.remove(opt);
        }
    }

    public void setErrorStreamType (int type) {
        // Fake it.
    }

    /**
     * Set the error level to abort formatting process.
     * <br>XSL Formatter will stop formatting when the detected error level is equal to setExitLevel setting or higher.
     * <br>The default value is 2 (Warning). Thus if an error occurred and error level is 2 (Warning) or higher, the
     * formatting process will be aborted. Please use the value from 1 to 4. When the value of 5 or more is specified,
     * it is considered to be the value of 4. If a error-level:4 (fatal error) occurs, the formatting process will be
     * aborted unconditionally. Note: An error is not displayed regardless what value may be specified to be this property.
     *
     * @param level Error level to abort
     * @throws jp.co.antenna.XfoJavaCtl.XfoException
     */
    public void setExitLevel (int level) throws XfoException {
        // Set the level...
        String opt = "-extlevel";
        if (this.args.containsKey(opt))
            this.args.remove(opt);
        this.args.put(opt, String.valueOf(level));
    }

    /**
     * Set the command-line string for external XSLT processor. For example:
     * <DL><DD>xslt -o %3 %1 %2 %param</DL>
     * %1 to %3 means following:
     * <DL><DD>%1 : XML Document
     * <DD>%2 : XSL Stylesheet
     * <DD>%3 : XSLT Output File
     * <DD>%param : xsl:param</DL>
     * %1 to %3 are used to express only parameter positions. Do not replace them with actual file names.
     * <br>In case you use XSL:param for external XSLT processor, set the name and value here.
     * <br>In Windows version, default MSXML3 will be used.
     *
     * @param cmd Command-line string for external XSLT processor
     * @throws jp.co.antenna.XfoJavaCtl.XfoException
     */
    public void setExternalXSLT (String cmd) throws XfoException {
        // Fill this in....
    }

    /**
     * Register the MessageListener interface to the instance of implemented class.
     * <br>The error that occurred during the formatting process can be received as the event.
     *
     * @param listener The instance of implemented class
     */
    public void setMessageListener (MessageListener listener) {
        if (listener != null)
            this.messageListener = listener;
        else
            this.messageListener = null;
    }

    public void setMultiVolume (boolean multiVol) {
        String opt = "-multivol";
        if (multiVol) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    /**
     * Specifies the output file path of the formatted result.
     * <br>When the printer is specified as an output format by setPrinterName, a
     * printing result is saved to the specified file by the printer driver.
     * <br>When output format other than a printer is specified, it is saved at the
     * specified file with the specified output format.
     * <br>When omitted, or when "@STDOUT" is specified, it comes to standard output.
     *
     * @param path Path name of output file
     * @throws jp.co.antenna.XfoJavaCtl.XfoException
     */
    public void setOutputFilePath (String path) throws XfoException {
        // Set the path...
        String opt = "-o";
        if (path != null && !path.equals("")) {
        outputFilename = path;
            if (this.args.containsKey(opt)) {
                this.args.remove(opt);
        }
        this.args.put(opt, path);
        }
        else {
        outputFilename = "";
            this.args.remove(opt);
        }
    }

    public void setWorkingDir(File workDir) {
    this.workingDir = workDir;
    }

    public void setFormatterType (int formatterType) {
        String opt = "-f";
        if (this.args.containsKey(opt))
            this.args.remove(opt);
        switch (formatterType) {
            case 0: this.args.put(opt, "AUTO"); break;
            case 1: this.args.put(opt, "HTML"); break;
            case 2: this.args.put(opt, "XHTML"); break;
            case 3: this.args.put(opt, "XMLCSS"); break;
            case 4: this.args.put(opt, "XSLFO"); break;
        }
    }

    public void setHtmlDefaultCharset (String charset) {
        String opt = "-htmlcs";
        if (charset != null && !charset.equals("")) {
            if (this.args.containsKey(opt))
                this.args.remove(opt);
            this.args.put(opt, charset);
        }
        else {
            this.args.remove(opt);
        }
    }

    public void addUserStylesheetURI (String uri) {
        if (uri != null && !uri.equals(""))
            this.userCSS.add(uri);
    }

    public void setPdfEmbedAllFontsEx (int embedLevel) throws XfoException {
        // fill it in
        String opt = "-peb";
        if (embedLevel != -1) {
            this.args.put(opt, String.valueOf(embedLevel));
        } else {
            this.args.remove(opt);
        }
    }

    public void setPdfImageCompression (int compressionMethod) {
        // fill it in
    }

    public void setPdfNoAccessibility (boolean newVal) {
        String opt = "-nab";
        if (newVal) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    public void setPdfNoAddingOrChangingComments (boolean newVal) {
        String opt = "-nca";
        if (newVal) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    public void setPdfNoAssembleDoc (boolean newVal) {
        String opt = "-nad";
        if (newVal) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    public void setPdfNoChanging (boolean newVal) {
        String opt = "-ncg";
        if (newVal) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    public void setPdfNoContentCopying (boolean newVal) {
        String opt = "-ncc";
        if (newVal) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    public void setPdfNoFillForm (boolean newVal) {
        String opt = "-nff";
        if (newVal) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    public void setPdfNoPrinting (boolean newVal) {
        String opt = "-npt";
        if (newVal) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    /**
     * Specifies the owner password for PDF. The password must be within 32 bytes.
     * Effective when outputting to PDF.
     *
     * @param newVal Owner password
     */
    public void setPdfOwnerPassword (String newVal) {
        String opt = "-ownerpwd";
        if (newVal != null && !newVal.equals("")) {
            if (this.args.containsKey(opt))
                this.args.remove(opt);
            this.args.put(opt, newVal);
        }
        else {
            this.args.remove(opt);
        }
    }

    public void setPdfTag (boolean newVal) {
        String opt = "-tpdf";
        if (newVal) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    public void setPdfVersion (int newVal) {
        String opt = "-pdfver";
        String version = null;
        if (this.args.containsKey(opt))
            this.args.remove(opt);

    switch (newVal) {
    case S_PDF_VERSION_13:
        version = "PDF1.3";
        break;
    case S_PDF_VERSION_14:
        version = "PDF1.4";
        break;
    case S_PDF_VERSION_15:
        version = "PDF1.5";
        break;
    case S_PDF_VERSION_16:
        version = "PDF1.6";
        break;
    case S_PDF_VERSION_17:
        version = "PDF1.7";
        break;
    case S_PDF_VERSION_20:
        version = "PDF2.0";
        break;

    case S_PDF_VERSION_A_1a_2005:
        version = "PDF/A-1a:2005";
        break;
    case S_PDF_VERSION_A_1b_2005:
        version = "PDF/A-1b:2005";
        break;

    case S_PDF_VERSION_A_2a_2011_14:
        version = "PDF1.4/A-2a:2011";
        break;
    case S_PDF_VERSION_A_2a_2011_15:
        version = "PDF1.5/A-2a:2011";
        break;
    case S_PDF_VERSION_A_2a_2011_16:
        version = "PDF1.6/A-2a:2011";
        break;
    case S_PDF_VERSION_A_2a_2011_17:
        version = "PDF1.7/A-2a:2011";
        break;

    case S_PDF_VERSION_A_2b_2011_14:
        version = "PDF1.4/A-2b:2011";
        break;
    case S_PDF_VERSION_A_2b_2011_15:
        version = "PDF1.5/A-2b:2011";
        break;
    case S_PDF_VERSION_A_2b_2011_16:
        version = "PDF1.6/A-2b:2011";
        break;
    case S_PDF_VERSION_A_2b_2011_17:
        version = "PDF1.7/A-2b:2011";
        break;

    case S_PDF_VERSION_A_2u_2011_14:
        version = "PDF1.4/A-2u:2011";
        break;
    case S_PDF_VERSION_A_2u_2011_15:
        version = "PDF1.5/A-2u:2011";
        break;
    case S_PDF_VERSION_A_2u_2011_16:
        version = "PDF1.6/A-2u:2011";
        break;
    case S_PDF_VERSION_A_2u_2011_17:
        version = "PDF1.7/A-2u:2011";
        break;

    case S_PDF_VERSION_A_3a_2012_14:
        version = "PDF1.4/A-3a:2012";
        break;
    case S_PDF_VERSION_A_3a_2012_15:
        version = "PDF1.5/A-3a:2012";
        break;
    case S_PDF_VERSION_A_3a_2012_16:
        version = "PDF1.6/A-3a:2012";
        break;
    case S_PDF_VERSION_A_3a_2012_17:
        version = "PDF1.7/A-3a:2012";
        break;

    case S_PDF_VERSION_A_3b_2012_14:
        version = "PDF1.4/A-3b:2012";
        break;
    case S_PDF_VERSION_A_3b_2012_15:
        version = "PDF1.5/A-3b:2012";
        break;
    case S_PDF_VERSION_A_3b_2012_16:
        version = "PDF1.6/A-3b:2012";
        break;
    case S_PDF_VERSION_A_3b_2012_17:
        version = "PDF1.7/A-3b:2012";
        break;

    case S_PDF_VERSION_A_3u_2012_14:
        version = "PDF1.4/A-3u:2012";
        break;
    case S_PDF_VERSION_A_3u_2012_15:
        version = "PDF1.5/A-3u:2012";
        break;
    case S_PDF_VERSION_A_3u_2012_16:
        version = "PDF1.6/A-3u:2012";
        break;
    case S_PDF_VERSION_A_3u_2012_17:
        version = "PDF1.7/A-3u:2012";
        break;

    case S_PDF_VERSION_UA1_15:
        version = "PDF1.5/UA-1:2014";
        break;
    case S_PDF_VERSION_UA1_16:
        version = "PDF1.6/UA-1:2014";
        break;
    case S_PDF_VERSION_UA1_17:
        version = "PDF1.7/UA-1:2014";
        break;

    case S_PDF_VERSION_X_1a_2001:
        version = "PDF/X-1a:2001";
        break;
    case S_PDF_VERSION_X_1a_2003:
        version = "PDF/X-1a:2003";
        break;
    case S_PDF_VERSION_X_2_2003:
        version = "PDF/X-2:2003";
        break;
    case S_PDF_VERSION_X_3_2002:
        version = "PDF/X-3:2002";
        break;
    case S_PDF_VERSION_X_3_2003:
        version = "PDF/X-3:2003";
        break;
    case S_PDF_VERSION_X_4_2010:  //  includes S_PDF_VERSION_X_4_2008 compat
        version = "PDF/X-4:2010";
        break;

    case S_PDF_VERSION_X_4p_2010:
        version = "PDF/X-4p:2010";
        break;
        }

        if (version != null)
            this.args.put(opt, version);
    }

    public void setPrinterName (String prn) {
        String opt = "-p";
        if (prn != null && !prn.equals("")) {
            if (this.args.containsKey(opt))
                this.args.remove(opt);
            this.args.put(opt, prn);
        }
        else {
            this.args.remove(opt);
        }
    }

    public void setStylesheetURI (String uri) {
        String opt = "-s";
        if (uri != null && !uri.equals("")) {
            if (this.args.containsKey(opt))
                this.args.remove(opt);
            this.args.put(opt, uri);
        }
        else {
            this.args.remove(opt);
        }
    }

    /**
     * Specifies the two pass format.
     *
     * @param val specification of two pass format
     */
    public void setTwoPassFormatting (boolean val) {
        String opt = "-2pass";
        if (val) {
            this.args.put(opt, null);
        } else {
            this.args.remove(opt);
        }
    }

    /**
     * Get the specification of two pass format.
     *
     * @return specification of two pass format.
     */
    public boolean getTwoPassFormatting () {
        if (this.args.containsKey("-2pass")) {
            return true;
        }
        return false;
    }

    /**
     * Get all AHF environment variables created by default in the XfoObj() constructor.
     *
     * @return an ArrayList object with all the AHF default environment variables
     */
    public ArrayList<String> getEnvironmentVariables () {
        return envp;
    }

    /**
     * Adds an environment variable to the ArrayList of AHF default environment variables created in the XfoObj() constructor.
     *
     * The 'key' is auto-prefixed with the appropriate environment variable prefix for the formatter version
     * defined in the XfoObj() constructor.
     *
     * Example: Adding the key 'XSLT_COMMAND' (which is not set in the AHF default environment variables) results
     * in an environment variable exported as 'AHFxx_64_XSLT_COMMAND' where the prefix 'AHFxx_' or the 64bit version
     * 'AHFxx_64_' is determined in the XfoObj() constructor.
     *
     * @param key the environment variable name without the AHF version prefix
     * @param value the value
     */
    public void addAhfEnvironmentVariable (String key, String value) {
        envp.add(envStart + "_" + key + "=" + value);
    }

    /**
     * Overrides an existing environment variable in the ArrayList of AHF default environment variables created
     * in the XfoObj() constructor.
     *
     * The 'key' is auto-prefixed with the appropriate environment variable prefix for the formatter version
     * defined in the XfoObj() constructor.
     *
     * Example: Calling the method with the key 'HYPDIC_PATH' overrides the default environment variable
     * 'AHFxx_HYPDIC_PATH' or the 64bit version 'AHFxx_64_HYPDIC_PATH' with your own value. The prefix 'AHFxx_'
     * or the 64bit version 'AHFxx_64_' is determined in the XfoObj() constructor.
     *
     * @param key the environment variable name without the AHF version prefix
     * @param value the value
     */
    public void overrideDefaultAhfEnvironmentVariable (String key, String value) {
        for (int i = 0; i < envp.size(); i++) {
            String e = envp.get(i);

            if(e.contains(key)) {
                envp.set(i, e.split("=")[0] + "=" + value);
            }
        }
    }

    public void setOptionFileURI (String path) {
        String opt = "-i";
        if (path != null && !path.equals("")) {
            if (this.args.containsKey(opt))
                this.args.remove(opt);
            this.args.put(opt, path);
        }
        else {
            this.args.remove(opt);
        }
    }

    public void addOptionFileURI (String path) {
        this.setOptionFileURI(path);
    }

    public void setXSLTParam (String paramName, String value) {
        // fill it in
    }

    public void setStartPage (int num) throws XfoException {
        String opt = "-start";
        if (this.args.containsKey(opt))
            this.args.remove(opt);
        this.args.put(opt, String.valueOf(num));
    }

    public void setEndPage (int num) throws XfoException {
        String opt = "-end";
        if (this.args.containsKey(opt))
            this.args.remove(opt);
        this.args.put(opt, String.valueOf(num));
    }

    /*
    protected void finalize () throws Throwable {
    try {
        System.out.println("testing axfo finalize()");
    } finally {
        super.finalize();
    }
    }
    */
}

class StreamCopyThread extends Thread {
    private InputStream inStream;
    private OutputStream outStream;

    public StreamCopyThread (InputStream inStream, OutputStream outStream) {
    this.inStream = inStream;
    this.outStream = outStream;
    }

    @Override
    public void run () {
    try {
        int COUNT = 1024;
        byte[] buff = new byte[COUNT];
        int len;
        while ((len = this.inStream.read(buff, 0, COUNT)) != -1) {
        this.outStream.write(buff, 0, len);
        }
    } catch (Exception e) {
        System.err.println("Exception copying streams: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {inStream.close();} catch (Exception e) {
        System.err.println("Exception closing input stream: " + e.getMessage());
        }
        try {outStream.close();} catch (Exception e) {
        System.err.println("Exception closing output stream: " + e.getMessage());
        }
    }
    }
}

class StreamFlusher extends Thread {
    private InputStream stream;

    public StreamFlusher (InputStream stream) {
    this.stream = stream;
    }

    @Override
    public void run () {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    try {
        String line = reader.readLine();
        while (line != null) {
        line = reader.readLine();
        }
    } catch (IOException e) {
        System.err.println("IOException flushing stream: " + e.getMessage());
    }
    }
}

class ErrorParser extends Thread {
    private InputStream ErrorStream;
    private MessageListener listener;
    public int LastErrorLevel;
    public int LastErrorCode;
    public String LastErrorMessage;
    public String UnknownErrorMessage = "";

    public int majorVersion = 0;
    public int minorVersion = 0;
    public String revision = "";  // ex:  r1, mr9, etc.

    public ErrorParser (InputStream ErrorStream, MessageListener listener) {
        this.ErrorStream = ErrorStream;
        this.listener = listener;
    }

    @Override
    public void run () {
        try {
        boolean errorParsed = false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.ErrorStream));
            String line = reader.readLine();
        String fullMessage;

        //System.err.println(line);
        fullMessage = line + "\n";

            while (line != null) {
        // check for version
        // AHFCmd : AH Formatter V6.2 MR1 for Linux : 6.2.3.16772 (2014/04/30 10:59JST)
        // can also be of the form:
        // AHFCmd : AH XSL Formatter V6.0 MR7 for Windows : 6.0.8.9416 (2013/02/26 10:36JST)
        // can't depend on the version token being at a split word
        // position

        if (revision.equals("")  &&
            (line.startsWith("XSLCmd : XSL ")  ||  line.startsWith("AHFCmd : AH "))) {
            String[] words = line.split(" ");

            for (int i = 0;  i < words.length - 1;  i++) {
            String word = words[i];
            if (word.length() > 3  &&  word.charAt(0) == 'V' &&  Character.isDigit(word.charAt(1))) {
                // possibly got the version string
                String[] vs = word.substring(1).split("\\.");
                if (vs.length < 2) {
                System.err.println("axfo: couldn't get version string");
                } else {
                try {
                    majorVersion = Integer.parseInt(vs[0]);
                    minorVersion = Integer.parseInt(vs[1]);
                    revision = words[i + 1];
                    //System.out.println("Formatter version: " + majorVersion + " " + minorVersion);
                } catch (NumberFormatException e) {
                    System.err.println("axfo: couldn't parse version " + vs[0] + " " + vs[1]);
                }
                }
            }
            }
        }

        if (line.startsWith("XSLCmd :") || line.startsWith("AHFCmd :")) {
                    if (line.contains("Error Level")) {
                        try {
                            int ErrorLevel = Integer.parseInt(line.substring(line.length() - 1, line.length()));
                            line = reader.readLine();
                            int ErrorCode = Integer.parseInt(line.split(" ")[line.split(" ").length - 2]);
                            line = reader.readLine();
                            String ErrorMessage = line.split(" ", 3)[2];
                            line = reader.readLine();
                            //FIXME redundant check for startsWith()
                            if (line.startsWith("XSLCmd :") || line.startsWith("AHFCmd :")) {
                                ErrorMessage += "\n" + line.split(" ", 3)[2];
                            }
                            this.LastErrorLevel = ErrorLevel;
                            this.LastErrorCode = ErrorCode;
                            this.LastErrorMessage = ErrorMessage;
                if (this.listener != null)
                this.listener.onMessage(ErrorLevel, ErrorCode, ErrorMessage);
                errorParsed = true;
                        } catch (Exception e) {
                System.err.println("Exception in error parser: " + e.getMessage());
                //FIXME throw exception?
            }
                    }
                } else if (line.startsWith("Invalid license.")) {
            int ErrorLevel = 4;
            //FIXME what version is this error code from
            int ErrorCode = 24579;
            String ErrorMessage = line
            + "\n" + reader.readLine()
            + "\n" + reader.readLine();
            this.LastErrorLevel = ErrorLevel;
            this.LastErrorCode = ErrorCode;
            this.LastErrorMessage = ErrorMessage;
            if (this.listener != null)
            this.listener.onMessage(ErrorLevel, ErrorCode, ErrorMessage);
            errorParsed = true;

                } else if (line.startsWith("Evaluation license is expired:")) {
            int ErrorLevel = 4;
            int ErrorCode = 24591;
            String ErrorMessage = line;
            this.LastErrorLevel = ErrorLevel;
            this.LastErrorCode = ErrorCode;
            this.LastErrorMessage = ErrorMessage;
            if (this.listener != null)
            this.listener.onMessage(ErrorLevel, ErrorCode, ErrorMessage);
            errorParsed = true;
        } else {
            // unknown error
            UnknownErrorMessage += line + "\n";
        }

                line = reader.readLine();
        if (line != null) {
            fullMessage += line + "\n";
        } else {

        }
            }

        if (!errorParsed) {
        this.LastErrorCode = 0;
        this.LastErrorMessage = fullMessage;
        if (this.listener != null) {
            //this.listener.onMessage(-1, -1, this.LastErrorMessage);
        }
        }
        } catch (Exception e) {
        System.err.println("Exception in error parsing thread: " + e.getMessage());
        //FIXME throw exception
    }
    }
}
