/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.co.antenna.XfoJavaCtl;

import java.io.*;
import java.util.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 * XfoObj Class is the object class of XSL Formatter
 *
 * @author Test User
 */
public class XfoObj {
    // Consts
    public static final int EST_NONE = 0;
    public static final int EST_STDOUT = 1;
    public static final int EST_STDERR = 2;
    private static final String[] AH_HOME_ENV = {
	"AHF71_64_HOME", "AHF71_HOME",
	"AHF70_64_HOME", "AHF70_HOME",
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
    private String executable;
    private MessageListener messageListener;
    private LinkedHashMap<String, String> args;
    private ArrayList<String> userCSS;
    private XfoException lastError;
    private String os;
    private boolean isWindows;
    private String axf_home = null;

    // Methods

    /* assumes keys are of type AHFxxx_HOME where 'x's are integers */
    int parseFormatterVersionFromKey (String key) {
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

    /**
     * Create the instance of XfoObj, and initialize it.
     *
     * @param preferredHome  Use a specific version of Formatter.  This is
     * specified as a Formatter home environment variable (ex: AHF63_HOME,
     * AHF62_64_HOME, etc.) and needs to already be defined in the user's
     * environment.  In addition to using the executable found with this
     * variable the Formatter process that is launched alters the PATH and
     * LD_LIBRARY_PATH settings to match the specified environment.
     * @throws XfoException
     */
    public XfoObj (String preferredHome) throws XfoException {
        // Check EVs and test if XslCmd.exe exists.
	this.preferredHome = preferredHome;

	try {
	    os = System.getProperty("os.name");
	    if ((os == null) || os.equals(""))
		throw new Exception();
	} catch (Exception e) {
	    throw new XfoException(4, 0, "Could not determine OS");
	}

	isWindows = os.toLowerCase().contains("windows");

	int axf_ver = 1;
	Map<String, String> env = System.getenv();

	try {
	    if (preferredHome != null) {
		if (env.containsKey(preferredHome)) {
		    axf_home = env.get(preferredHome);
		}
	    }

	    if (axf_home == null  ||  axf_home.equals("")) {
		for (String key: AH_HOME_ENV) {
		    if (env.containsKey(key)) {
			axf_home = env.get(key);
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

	    if ((axf_home == null) || axf_home.equals(""))
		throw new Exception();
	} catch (Exception e) {
	    throw new XfoException(4, 1, "Could not locate Formatter's environment variables");
	}

	String separator = System.getProperty("file.separator");
	this.executable = axf_home + separator;
	if (os.equals("Linux") || os.equals("SunOS") || os.equals("AIX") || os.equals("Mac OS X")) {
	    if (axf_ver == 0)
		this.executable += "bin" + separator + "XSLCmd";
	    else
		this.executable += "bin" + separator + "AHFCmd";
	}
	else if (isWindows) {
	    if (axf_ver == 0)
		this.executable += "XSLCmd.exe";
	    else
		this.executable += "AHFCmd.exe";
	}
	else
	    throw new XfoException(4, 2, "Unsupported OS: " + os);
        // setup attributes
        this.clear();
    }

    public XfoObj () throws XfoException {
	this(null);
    }

    /**
     * Cleanup (initialize) XSL Formatter engine.
     */
    public void clear () {
        // reset attributes
        this.args = new LinkedHashMap<String, String>();
        this.messageListener = null;
	this.userCSS = new ArrayList<String>();
	this.lastError = null;
    }

    /**
     * Execute formatting and outputs to a PDF.
     *
     * @throws jp.co.antenna.XfoJavaCtl.XfoException
     */
    public void execute () throws XfoException {
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

	ProcessBuilder pb;
        Process process;
        ErrorParser errorParser = null;
        int exitCode = -1;
        try {
	    String[] s = new String[0];
	    pb = new ProcessBuilder(cmdArray.toArray(s));
	    Map<String, String> env = pb.environment();

	    if (preferredHome != null) {
		if (isWindows) {
		    String path = env.get("Path");

		    if (path == null) {
			path = "";
		    }

		    //System.out.println("path before: " + path);
		    env.put("Path", axf_home + ";" + path);
		    //System.out.println("path: " + env.get("Path"));
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
		    //System.out.println("ld path: " + env.get("LD_LIBRARY_PATH"));
		}
	    }

	    process = pb.start();
	    try {
		InputStream StdErr = process.getErrorStream();
		errorParser = new ErrorParser(StdErr, this.messageListener);
		errorParser.start();
	    } catch (Exception e) {}
            exitCode = process.waitFor();
	    errorParser.join();
        } catch (Exception e) {
	    e.printStackTrace();
	}

        if (exitCode != 0) {
            if (errorParser != null && errorParser.LastErrorCode != 0) {
                this.lastError = new XfoException(errorParser.LastErrorLevel, errorParser.LastErrorCode, errorParser.LastErrorMessage);
		throw this.lastError;
            } else {
                throw new XfoException(4, 0, "Failed to parse last error. Exit code: " + exitCode + ". " + errorParser.UnknownErrorMessage);
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
    public void render (InputStream src, OutputStream dst, String outDevice) throws XfoException {
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

	ProcessBuilder pb;
	Process process;
	ErrorParser errorParser = null;
	int exitCode = -1;

	try {
	    String[] s = new String[0];

	    pb = new ProcessBuilder(cmdArray.toArray(s));
	    Map<String, String> env = pb.environment();

	    if (preferredHome != null) {
		if (isWindows) {
		    String path = env.get("Path");

		    if (path == null) {
			path = "";
		    }

		    //System.out.println("path before: " + path);
		    env.put("Path", axf_home + ";" + path);
		    //System.out.println("path: " + env.get("Path"));
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
		    //System.out.println("ld path: " + env.get("LD_LIBRARY_PATH"));
		}
	    }

	    process = pb.start();

	    try {
		InputStream StdErr = process.getErrorStream();
		errorParser = new ErrorParser(StdErr, this.messageListener);
		errorParser.start();
		(new StreamCopyThread(process.getInputStream(), dst)).start();
		(new StreamCopyThread(src, process.getOutputStream())).start();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    exitCode = process.waitFor();
	    errorParser.join();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	if (exitCode != 0) {
	    if (errorParser != null && errorParser.LastErrorCode != 0) {
		this.lastError = new XfoException(errorParser.LastErrorLevel, errorParser.LastErrorCode, errorParser.LastErrorMessage);
		throw this.lastError;
	    } else {
		throw new XfoException(4, 0, "Failed to parse last error. Exit code: " + exitCode + ". " + errorParser.UnknownErrorMessage);
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
	    throw new XfoException(4, 0, "XSLT Transformation failed: " + e.toString());
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
            if (this.args.containsKey(opt))
                this.args.remove(opt);
            this.args.put(opt, path);
        }
        else {
            this.args.remove(opt);
        }
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
	    e.printStackTrace();
	} finally {
	    try {inStream.close();} catch (Exception e) {}
	    try {outStream.close();} catch (Exception e) {}
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

    public ErrorParser (InputStream ErrorStream, MessageListener listener) {
        this.ErrorStream = ErrorStream;
        this.listener = listener;
    }

    @Override
    public void run () {
        try {
            // stuff
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.ErrorStream));
            String line = reader.readLine();
            while (line != null) {
                if (line.startsWith("XSLCmd :") || line.startsWith("AHFCmd :")) {
                    if (line.contains("Error Level")) {
                        try {
                            int ErrorLevel = Integer.parseInt(line.substring(line.length() - 1, line.length()));
                            line = reader.readLine();
                            int ErrorCode = Integer.parseInt(line.split(" ")[line.split(" ").length - 2]);
                            line = reader.readLine();
                            String ErrorMessage = line.split(" ", 3)[2];
                            line = reader.readLine();
                            if (line.startsWith("XSLCmd :") || line.startsWith("AHFCmd :")) {
                                ErrorMessage += "\n" + line.split(" ", 3)[2];
                            }
                            this.LastErrorLevel = ErrorLevel;
                            this.LastErrorCode = ErrorCode;
                            this.LastErrorMessage = ErrorMessage;
			    if (this.listener != null)
				this.listener.onMessage(ErrorLevel, ErrorCode, ErrorMessage);
                        } catch (Exception e) {}
                    }
                } else if (line.startsWith("Invalid license.")) {
		    int ErrorLevel = 4;
		    int ErrorCode = 24579;
		    String ErrorMessage = line
			+ "\n" + reader.readLine()
			+ "\n" + reader.readLine();
		    this.LastErrorLevel = ErrorLevel;
		    this.LastErrorCode = ErrorCode;
		    this.LastErrorMessage = ErrorMessage;
		    if (this.listener != null)
			this.listener.onMessage(ErrorLevel, ErrorCode, ErrorMessage);
		} else if (line.startsWith("Evaluation license is expired:")) {
		    int ErrorLevel = 4;
		    int ErrorCode = 24591;
		    String ErrorMessage = line;
		    this.LastErrorLevel = ErrorLevel;
		    this.LastErrorCode = ErrorCode;
		    this.LastErrorMessage = ErrorMessage;
		    if (this.listener != null)
			this.listener.onMessage(ErrorLevel, ErrorCode, ErrorMessage);
		} else {
		    UnknownErrorMessage += line + "\n";
		}

                line = reader.readLine();
            }
        } catch (Exception e) {}
    }
}
