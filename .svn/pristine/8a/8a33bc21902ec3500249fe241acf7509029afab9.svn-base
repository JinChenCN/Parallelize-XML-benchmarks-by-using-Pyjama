//Pyjama compiler version:v1.5.3
package spec.benchmarks.xml.transform;

import pj.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import org.xml.sax.SAXException;
import spec.benchmarks.xml.XMLBenchmark;
import spec.harness.Constants;
import spec.harness.Context;
import spec.harness.Launch;
import spec.harness.Util;
import spec.harness.results.BenchmarkResult;
import spec.io.FileCache;
import spec.io.FileCache.CachedFile;
import java.util.Properties;

import pj.pr.*;
import pj.PjRuntime;
import pj.Pyjama;
import pi.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;
import pj.pr.exceptions.OmpParallelRegionLocalCancellationException;

public class Main extends XMLBenchmark {

    private static final int LONG_VALIDATION_MODE = 0;

    private static final int SHORT_VALIDATION_MODE = 1;

    private static final int SINGLE_LOOP_MODE = 0;

    private static final int MULTIPLE_LOOP_MODE = 1;

    private static final int INPUT_PAIR = 10;

    private static final String CONTROL_FILE_NAME = "transformations.properties";

    static String OUT_DIR = "xml_out";

    private static int THREADSNUM = 4;

    private static final String[] XML_NAMES = { "chess-fo/Kasparov-Karpov.xml", "jenitennison/index.xml", "jenitennison/text.xml", "nitf/nitf-fishing.xml", "shared/REC-xml-19980210.xml", "recipes/recipes.xml", "dsd/article.xml", "renderx/chess/Kasparov-Karpov.xml", "renderx/examples/balance/balance_sheet.xml", "renderx/examples/meeting/meeting_minutes.xml" };

    private static final String[] XSL_NAMES = { "chess-fo/chess.xsl", "jenitennison/page.xsl", "jenitennison/markup.xsl", "nitf/nitf-stylized.xsl", "spec-html/xmlspec.xsl", "recipes/recipes.xsl", "dsd/article2html.xsl", "renderx/chess/chess.xsl", "renderx/examples/balance/balance_sheet.xsl", "renderx/examples/meeting/meeting_minutes.xsl" };

    private static final int[] loops = { 2, 18, 31, 34, 1, 10, 12, 3, 11, 23 };

    private static FileCache.CachedFile[] xmlInput = null;

    private static FileCache.CachedFile[] xslInput = null;

    private static int validationMode = LONG_VALIDATION_MODE;

    private static int loopMode = SINGLE_LOOP_MODE;

    private static Properties longValidationProperties = null;

    private static Properties[][][] shortValidationProperties = null;

    private static String validationFileName = null;

    private static Transformer[][] allTransformers = null;

    private int threadId = 0;

    private static void setValidationMode(int mode) {{
        validationMode = mode;
    }
    }


    private static int getValidationMode() {{
        return validationMode;
    }
    }


    private static void setLoopMode(int mode) {{
        loopMode = mode;
    }
    }


    private static int getLoopMode() {{
        return loopMode;
    }
    }


    public static String testType() {{
        return MULTI;
    }
    }


    public static void setupBenchmark() {{
        String tmpName = Util.getProperty(Constants.XML_TRANSFORM_OUT_DIR_PROP, null);
        OUT_DIR = tmpName != null ? tmpName : OUT_DIR;
        File file = new File(OUT_DIR);
        validationFileName = getFullName(Main.class, null, CONTROL_FILE_NAME);
        xmlInput = new FileCache.CachedFile[INPUT_PAIR];
        xslInput = new FileCache.CachedFile[INPUT_PAIR];
        for (int i = 0; i < INPUT_PAIR; i++) {
            xmlInput[i] = getCachedFile(Main.class, null, XML_NAMES[i]);
            xslInput[i] = getCachedFile(Main.class, null, XSL_NAMES[i]);
        }
        longValidationProperties = new Properties();
        try {
            if (!file.exists()) {
                file.mkdir();
            }
            longValidationProperties.load(new FileInputStream(validationFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupTransformers();
        setValidationMode(LONG_VALIDATION_MODE);
        setLoopMode(SINGLE_LOOP_MODE);
        shortValidationProperties = new Properties[Launch.currentNumberBmThreads][INPUT_PAIR][3];
        Main main = new Main(new BenchmarkResult(), 1);
        main.harnessMain();
        int threads = Launch.currentNumberBmThreads;
        setValidationMode(SHORT_VALIDATION_MODE);
        setLoopMode(MULTIPLE_LOOP_MODE);
    }
    }


    public static void tearDownBenchmark() {{
        if (!ExtOutputStream.wasFullVerificationError && !Util.getBoolProperty(Constants.XML_TRANSFORM_LEAVE_OUT_DIR_PROP, null)) {
            remove(new File(OUT_DIR));
        }
    }
    }


    private static void setupTransformers() {{
        allTransformers = new Transformer[Launch.currentNumberBmThreads][INPUT_PAIR];
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            for (int i = 0; i < INPUT_PAIR; i++) {
                Templates precompiledTemplates = transformerFactory.newTemplates(xslInput[i].asNewStreamSource());
                for (int j = 0; j < Launch.currentNumberBmThreads; j++) {
                    allTransformers[j][i] = precompiledTemplates.newTransformer();
                }
            }
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }


    private Transformer[] precompiledTransformers = null;

    private StreamResult streamResult = null;

    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
        this.threadId = threadId;
        precompiledTransformers = allTransformers[threadId - 1];
    }

    public void harnessMain() {{
        try {
            for (int i = 0; i < 3; i++) {
                executeWorkload();
            }
        } catch (Exception e) {
            e.printStackTrace(Context.getOut());
        }
    }
    }


    public static void main(String[] args) throws Exception {{
        long start = System.currentTimeMillis();
        runSimple(Main.class, args);
        long time = System.currentTimeMillis() - start;
        System.out.println("PJ Parallel xml transform has taken  " + (time / 1000.0) + " seconds.");
    }
    }


    private Properties getOutProperties(BaseOutputStream outputStream) {{
        if (outputStream instanceof ExtOutputStream) {
            return ((ExtOutputStream) outputStream).getOutProperties();
        }
        return null;
    }
    }


    private void executeWorkload() throws TransformerException, ParserConfigurationException, SAXException, IOException {{
        Pyjama.omp_set_num_threads(THREADSNUM);
        /*OpenMP Parallel region (#0) -- START */
        InternalControlVariables icv_previous__OMP_ParallelRegion_0 = PjRuntime.getCurrentThreadICV();
        InternalControlVariables icv__OMP_ParallelRegion_0 = PjRuntime.inheritICV(icv_previous__OMP_ParallelRegion_0);
        int _threadNum__OMP_ParallelRegion_0 = icv__OMP_ParallelRegion_0.nthreads_var.get(icv__OMP_ParallelRegion_0.levels_var);
        ConcurrentHashMap<String, Object> inputlist__OMP_ParallelRegion_0 = new ConcurrentHashMap<String,Object>();
        ConcurrentHashMap<String, Object> outputlist__OMP_ParallelRegion_0 = new ConcurrentHashMap<String,Object>();
        _OMP_ParallelRegion_0 _OMP_ParallelRegion_0_in = new _OMP_ParallelRegion_0(_threadNum__OMP_ParallelRegion_0,icv__OMP_ParallelRegion_0,inputlist__OMP_ParallelRegion_0,outputlist__OMP_ParallelRegion_0);
        _OMP_ParallelRegion_0_in.runParallelCode();
        PjRuntime.recoverParentICV(icv_previous__OMP_ParallelRegion_0);
        RuntimeException OMP_ee_0 = (RuntimeException) _OMP_ParallelRegion_0_in.OMP_CurrentParallelRegionExceptionSlot.get();
        if (OMP_ee_0 != null) {throw OMP_ee_0;}
        /*OpenMP Parallel region (#0) -- END */

    }
    }
        class _OMP_ParallelRegion_0{
                private int OMP_threadNumber = 1;
                private InternalControlVariables icv;
                private ConcurrentHashMap<String, Object> OMP_inputList = new ConcurrentHashMap<String, Object>();
                private ConcurrentHashMap<String, Object> OMP_outputList = new ConcurrentHashMap<String, Object>();
                private ReentrantLock OMP_lock;
                private ParIterator<?> OMP__ParIteratorCreator;
                public AtomicReference<Throwable> OMP_CurrentParallelRegionExceptionSlot = new AtomicReference<Throwable>(null);

                //#BEGIN shared variables defined here
                //#END shared variables defined here
                public _OMP_ParallelRegion_0(int thread_num, InternalControlVariables icv, ConcurrentHashMap<String, Object> inputlist, ConcurrentHashMap<String, Object> outputlist) {
                    this.icv = icv;
                    if ((false == Pyjama.omp_get_nested()) && (Pyjama.omp_get_level() > 0)) {
                        this.OMP_threadNumber = 1;
                    }else {
                        this.OMP_threadNumber = thread_num;
                    }
                    this.OMP_inputList = inputlist;
                    this.OMP_outputList = outputlist;
                    icv.currentParallelRegionThreadNumber = this.OMP_threadNumber;
                    icv.OMP_CurrentParallelRegionBarrier = new PjCyclicBarrier(this.OMP_threadNumber);
                    //#BEGIN shared variables initialised here
                    //#END shared variables initialised here
                }

                private void updateOutputListForSharedVars() {
                    //BEGIN update outputlist
                    //END update outputlist
                }
                class MyCallable implements Callable<ConcurrentHashMap<String,Object>> {
                    private int alias_id;
                    private ConcurrentHashMap<String, Object> OMP_inputList;
                    private ConcurrentHashMap<String, Object> OMP_outputList;
                    //#BEGIN private/firstprivate reduction variables defined here
                    //#END private/firstprivate reduction variables  defined here
                    MyCallable(int id, ConcurrentHashMap<String,Object> inputlist, ConcurrentHashMap<String,Object> outputlist){
                        this.alias_id = id;
                        this.OMP_inputList = inputlist;
                        this.OMP_outputList = outputlist;
                        //#BEGIN firstprivate reduction variables initialised here
                        //#END firstprivate reduction variables initialised here
                    }

                    @Override
                    public ConcurrentHashMap<String,Object> call() {
                        try {
                            /****User Code BEGIN***/
                            /*OpenMP Work Share region (#0) -- START */
                            
                {//#BEGIN firstprivate lastprivate reduction variables defined and initialized here
                    //#set implicit barrier here, otherwise unexpected initial value happens
                    PjRuntime.setBarrier();
                    //#END firstprivate lastprivate reduction variables defined and initialized here
                    try{
                        int i=0;
                        int OMP_iterator = 0;
                        int OMP_end = (int)((INPUT_PAIR)-(0))/(1);
                        if (((INPUT_PAIR)-(0))%(1) == 0) {
                            OMP_end = OMP_end - 1;
                        }
                        int OMP_local_iterator = 0;
                        int OMP_Chunk_Starting_point = 0;
                        int OMP_Default_chunkSize_autoGenerated = (OMP_end+1)/Pyjama.omp_get_num_threads();
                        if (Pyjama.omp_get_thread_num() < (OMP_end+1) % Pyjama.omp_get_num_threads()) {
                            ++OMP_Default_chunkSize_autoGenerated;
                            OMP_Chunk_Starting_point = Pyjama.omp_get_thread_num() * OMP_Default_chunkSize_autoGenerated;
                        } else {
                            OMP_Chunk_Starting_point = Pyjama.omp_get_thread_num() * OMP_Default_chunkSize_autoGenerated + (OMP_end+1) % Pyjama.omp_get_num_threads();
                        }
                        for (OMP_local_iterator=OMP_Chunk_Starting_point; OMP_local_iterator<OMP_Chunk_Starting_point+OMP_Default_chunkSize_autoGenerated && OMP_Default_chunkSize_autoGenerated>0; ++OMP_local_iterator) {
                            i = 0 + OMP_local_iterator * (1);
                            {
                                String propertyNamePrefix = XML_NAMES[i] + ".";
                                int loops = (getLoopMode() == SINGLE_LOOP_MODE) ? 1 : Main.loops[i];
                                Transformer transformer = precompiledTransformers[i];
                                try {
                                    doTransform(loops, xmlInput[i], transformer, propertyNamePrefix, i);
                                } catch (SAXException e) {
                                    e.printStackTrace(Context.getOut());
                                } catch (IOException e) {
                                    e.printStackTrace(Context.getOut());
                                } catch (Exception e) {
                                    e.printStackTrace(Context.getOut());
                                }
                            }if (OMP_end == OMP_local_iterator) {
                                //BEGIN lastprivate variables value set
                                //END lastprivate variables value set
                            }
                        }
                    } catch (pj.pr.exceptions.OmpWorksharingLocalCancellationException wse){
                    } catch (Exception e){throw e;}
                    //BEGIN  reduction
                    PjRuntime.reductionLockForWorksharing.lock();
                    PjRuntime.reductionLockForWorksharing.unlock();//END reduction
                    PjRuntime.setBarrier();
                }

                            PjRuntime.setBarrier();
                            PjRuntime.reset_OMP_orderCursor();
                            /*OpenMP Work Share region (#0) -- END */

                            /****User Code END***/
                            //BEGIN reduction procedure
                            //END reduction procedure
                            PjRuntime.setBarrier();
                        } catch (OmpParallelRegionLocalCancellationException e) {
                         	PjRuntime.decreaseBarrierCount();
                        } catch (Exception e) {
                            PjRuntime.decreaseBarrierCount();
                        	PjExecutor.cancelCurrentThreadGroup();
                        OMP_CurrentParallelRegionExceptionSlot.compareAndSet(null, e);
                    }
                    if (0 == this.alias_id) {
                        updateOutputListForSharedVars();
                    }
                    return null;
                }
            }
            public void runParallelCode() {
                for (int i = 1; i <= this.OMP_threadNumber-1; i++) {
                    Callable<ConcurrentHashMap<String,Object>> slaveThread = new MyCallable(i, OMP_inputList, OMP_outputList);
                    PjRuntime.submit(i, slaveThread, icv);
                }
                Callable<ConcurrentHashMap<String,Object>> masterThread = new MyCallable(0, OMP_inputList, OMP_outputList);
                PjRuntime.getCurrentThreadICV().currentThreadAliasID = 0;
                try {
                    masterThread.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }




    private void doTransform(int loops, CachedFile xmlInput, Transformer transformer, String propertyNamePrefix, int INPUT_PAIR_num) throws TransformerException, ParserConfigurationException, SAXException, IOException {{
        for (int j = loops - 1; j >= 0; j--) {
            transform(transformer, createSaxSource(xmlInput), propertyNamePrefix + "SAX", j, INPUT_PAIR_num);
            transform(transformer, createDomSource(xmlInput), propertyNamePrefix + "DOM", j, INPUT_PAIR_num);
            transform(transformer, xmlInput.asNewStreamSource(), propertyNamePrefix + "Stream", j, INPUT_PAIR_num);
        }
    }
    }


    private void transform(Transformer transformer, Source source, String descr, int loop, int INPUT_PAIR_num) throws TransformerException, ParserConfigurationException, SAXException, IOException {{
        transformer.reset();
        BaseOutputStream outputStream = null;
        StreamResult streamResult = null;
        int sourceType = source instanceof SAXSource ? 0 : (source instanceof DOMSource ? 1 : 2);
        if (getValidationMode() == LONG_VALIDATION_MODE) {
            outputStream = new ExtOutputStream();
            streamResult = new StreamResult(outputStream);
            outputStream.setValidationProperties(longValidationProperties);
        } else {
            outputStream = new BaseOutputStream();
            streamResult = new StreamResult(outputStream);
            outputStream.setValidationProperties(shortValidationProperties[threadId - 1][INPUT_PAIR_num][sourceType]);
        }
        outputStream.setCurrentProp(descr);
        transformer.transform(source, streamResult);
        outputStream.checkResult(loop);
        if ((getValidationMode() == LONG_VALIDATION_MODE) && (outputStream instanceof ExtOutputStream)) {
            int threads = Launch.currentNumberBmThreads;
            Properties outProperties = getOutProperties(outputStream);
            for (int i = 0; i < threads; i++) {
                shortValidationProperties[i][INPUT_PAIR_num][sourceType] = (Properties) outProperties.clone();
            }
        }
    }
    }

}
