//Pyjama compiler version:v1.5.3
package spec.benchmarks.xml.validation;

import pj.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import spec.harness.Constants;
import spec.harness.Context;
import spec.harness.Launch;
import spec.harness.Util;
import spec.harness.results.BenchmarkResult;
import spec.io.FileCache;
import spec.io.FileCache.CachedFile;
import spec.benchmarks.xml.XMLBenchmark;

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

    private static final int XSD_NUMBER = 48;

    private static FileCache.CachedFile[] allInstanceBytes = null;

    private static FileCache.CachedFile[] allSchemaBytes = null;

    private static Validator[][] allValidators = null;

    private static int THREADSNUM = 1;

    public static String testType() {{
        return MULTI;
    }
    }


    private static String[] schemaNames = { "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd", "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd", "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd", "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd", "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd", "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd", "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd", "validation_input.xsd", "periodic_table.xsd", "play.xsd", "structure.xsd", "po.xsd", "personal.xsd" };

    private static String[] instanceNames = { "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml", "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml", "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml", "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml", "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml", "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml", "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml", "validation_input.xml", "periodicxsd.xml", "much_adoxsd.xml", "structure.xml", "po.xml", "personal.xml" };

    private static int loops[] = { 1, 5, 3, 52, 647, 419, 1, 5, 3, 52, 647, 419, 1, 5, 3, 52, 647, 419, 1, 5, 3, 52, 647, 419, 1, 5, 3, 52, 647, 419, 1, 5, 3, 52, 647, 419, 1, 5, 3, 52, 647, 419, 1, 5, 3, 52, 647, 419 };

    public static void setupBenchmark() {{
        String dirName = Util.getProperty(Constants.XML_VALIDATION_INPUT_DIR_PROP, null);
        try {
            allInstanceBytes = new FileCache.CachedFile[XSD_NUMBER];
            FileCache cache = Context.getFileCache();
            for (int i = 0; i < XSD_NUMBER; i++) {
                String name = getFullName(Main.class, dirName, instanceNames[i]);
                allInstanceBytes[i] = cache.new CachedFile(name);
                allInstanceBytes[i].cache();
            }
            allSchemaBytes = new FileCache.CachedFile[XSD_NUMBER];
            for (int i = 0; i < XSD_NUMBER; i++) {
                String name = getFullName(Main.class, dirName, schemaNames[i]);
                allSchemaBytes[i] = cache.new CachedFile(name);
                allSchemaBytes[i].cache();
            }
            setupValidators(dirName);
        } catch (IOException e) {
            e.printStackTrace(Context.getOut());
        }
    }
    }


    private static void setupValidators(String dirName) {{
        int threads = Launch.currentNumberBmThreads;
        allValidators = new Validator[threads][XSD_NUMBER];
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            sf.setErrorHandler(null);
            for (int i = 0; i < XSD_NUMBER; i++) {
                String xsdFilename = getFullName(Main.class, dirName, schemaNames[i]);
                File tempURI = new File(xsdFilename);
                Schema precompSchema = null;
                if (tempURI.isAbsolute()) {
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), tempURI.toURI().toString()));
                } else {
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), xsdFilename));
                }
                for (int j = 0; j < threads; j++) {
                    allValidators[j][i] = precompSchema.newValidator();
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }


    private Validator[] schemaBoundValidator = null;

    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
        schemaBoundValidator = allValidators[threadId - 1];
    }

    public void harnessMain() {{
        try {
            long start = System.currentTimeMillis();
            executeWorkload();
            long time = System.currentTimeMillis() - start;
            System.out.println("PJ Parallel xml validation has taken  " + (time / 1000.0) + " seconds.");
        } catch (Exception e) {
            e.printStackTrace(Context.getOut());
        }
    }
    }


    public static void main(String[] args) throws Exception {{
        runSimple(Main.class, args);
    }
    }


    private void executeWorkload() throws ParserConfigurationException, IOException, SAXException {{
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
                        int OMP_end = (int)((XSD_NUMBER)-(0))/(1);
                        if (((XSD_NUMBER)-(0))%(1) == 0) {
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
                                Context.getOut().println("Validating " + instanceNames[i]);
                                try {
                                    doValidationTests(loops[i], allInstanceBytes[i], schemaBoundValidator[i]);
                                } catch (SAXException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
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




    private void doValidationTests(int loops, CachedFile file, Validator schemaValidator) throws ParserConfigurationException, IOException, SAXException {{
        for (int i = loops - 1; i >= 0; i--) {
            validateSource(i, createDomSource(file), schemaValidator);
            validateSource(i, createSaxSource(file), schemaValidator);
        }
    }
    }


    private void validateSource(int loop, Source source, Validator schemaValidator) {{
        schemaValidator.reset();
        schemaValidator.setErrorHandler(null);
        try {
            schemaValidator.validate(source);
        } catch (SAXException e) {
            Context.getOut().print("\tas " + source.getClass().getName());
            Context.getOut().println(" failed. (Incorrect result)" + Arrays.toString(loops));
            e.printStackTrace(Context.getOut());
        } catch (IOException e) {
            Context.getOut().println("Unable to validate due to IOException.");
        }
    }
    }

}
