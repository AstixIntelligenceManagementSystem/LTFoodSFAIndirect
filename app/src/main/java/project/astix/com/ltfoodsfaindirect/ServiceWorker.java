package project.astix.com.ltfoodsfaindirect;


import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import com.astix.Common.CommonInfo;

import org.json.JSONArray;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ServiceWorker {

    public int chkTblStoreListContainsRow = 1;
    private Context context;
    private static final String TAG = ServiceWorker.class.getSimpleName();
    //Live Path WebServiceAndroidParagSFATesting
    //public String UrlForWebService="http://115.124.126.184/WebServiceAndroidParagSFA/Service.asmx";

    //Testing Path
    //public String UrlForWebService="http://115.124.126.184/WebServiceAndroidParagSFATesting/Service.asmx";

    public static int flagExecutedServiceSuccesfully = 0;
    public String UrlForWebService = CommonInfo.WebServicePath.trim();


    Locale locale = new Locale("en", "UK");
    String pattern = "###.##";
    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
    //private ServiceWorker _activity;
    private ContextWrapper cw;
    String movie_name;
    String director;
    //int counts;
    public String currSysDate;
    public String SysDate;

    public int newStat = 0;
    public int timeout = 0;


    public ServiceWorker getallStores(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        String RouteType = "0";
        try {
            dbengine.open();
            String RouteID = dbengine.GetActiveRouteID();
            RouteType = dbengine.FetchRouteType(rID);
            dbengine.close();
            System.out.println("hi" + RouteType);
        } catch (Exception e) {
            System.out.println("error" + e);
        }
        dbengine.open();
        dbengine.Delete_tblStore_for_refreshDataButNotNewStore();
        dbengine.fndeleteStoreAddressMapDetailsMstr();

        final String SOAP_ACTION = "http://tempuri.org/GetStoreListMR";
        final String METHOD_NAME = "GetStoreListMR";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteType", RouteType);
            client.addProperty("SysDate", SysDate.toString());
            client.addProperty("AppVersionID", dbengine.AppVersionID.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            System.out.println("S1");

            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;


            int totalCount = responseBody.getPropertyCount();

            // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            NodeList tblUOMMstrNode = doc.getElementsByTagName("tblStoreListMaster");
            for (int i = 0; i < tblUOMMstrNode.getLength(); i++) {

                String StoreID = "0";
                String StoreName = "NA";
                Double StoreLatitude = 0.0;
                Double StoreLongitude = 0.0;
                String StoreType = "0";
                String LastTransactionDate = "NA";
                String LastVisitDate = "NA";
                int Sstat = 0;
                int IsClose = 0;
                int IsNextDat = 0;
                int StoreRouteID = 0;
                int StoreCatNodeId = 0;
                String PaymentStage = "0";
                int flgHasQuote = 0;
                int flgAllowQuotation = 0;
                int flgSubmitFromQuotation = 0;

                String flgGSTCapture = "1";
                String flgGSTCompliance = "0";
                String GSTNumber = "0";
                int RouteNodeType = 0;
                int flgGSTRecordFromServer = 0;
                String collectionRequired = "0.00";
                int flgOrderType = -1;

                String StoreIDPDAFromServer = "NA";


                String OwnerName = "NA";
                String StoreContactNo = "0000000000";
                String StoreCatType = "NA";
                String ForDate = dateVAL;
                String flgCaptureCompetitorPTR = "0";
                Element element = (Element) tblUOMMstrNode.item(i);

                if (!element.getElementsByTagName("StoreID").equals(null)) {

                    NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                    Element line = (Element) StoreIDNode.item(0);

                    if (StoreIDNode.getLength() > 0) {

                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("StoreIDPDA").equals(null)) {

                    NodeList StoreIDPDANode = element.getElementsByTagName("StoreIDPDA");
                    Element line = (Element) StoreIDPDANode.item(0);

                    if (StoreIDPDANode.getLength() > 0) {

                        StoreIDPDAFromServer = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("StoreName").equals(null)) {

                    NodeList StoreNameNode = element.getElementsByTagName("StoreName");
                    Element line = (Element) StoreNameNode.item(0);

                    if (StoreNameNode.getLength() > 0) {

                        StoreName = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("StoreLatitude").equals(null)) {

                    NodeList StoreLatitudeNode = element.getElementsByTagName("StoreLatitude");
                    Element line = (Element) StoreLatitudeNode.item(0);

                    if (StoreLatitudeNode.getLength() > 0) {

                        StoreLatitude = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));
                    }
                }


                if (!element.getElementsByTagName("StoreLongitude").equals(null)) {

                    NodeList StoreLongitudeNode = element.getElementsByTagName("StoreLongitude");
                    Element line = (Element) StoreLongitudeNode.item(0);

                    if (StoreLongitudeNode.getLength() > 0) {

                        StoreLongitude = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("StoreType").equals(null)) {

                    NodeList StoreTypeNode = element.getElementsByTagName("StoreType");
                    Element line = (Element) StoreTypeNode.item(0);

                    if (StoreTypeNode.getLength() > 0) {

                        StoreType = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("LastTransactionDate").equals(null)) {

                    NodeList LastTransactionDateNode = element.getElementsByTagName("LastTransactionDate");
                    Element line = (Element) LastTransactionDateNode.item(0);

                    if (LastTransactionDateNode.getLength() > 0) {

                        LastTransactionDate = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("LastVisitDate").equals(null)) {

                    NodeList LastVisitDateNode = element.getElementsByTagName("LastVisitDate");
                    Element line = (Element) LastVisitDateNode.item(0);

                    if (LastVisitDateNode.getLength() > 0) {

                        LastVisitDate = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("IsClose").equals(null)) {

                    NodeList IsCloseNode = element.getElementsByTagName("IsClose");
                    Element line = (Element) IsCloseNode.item(0);

                    if (IsCloseNode.getLength() > 0) {

                        IsClose = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }


                if (!element.getElementsByTagName("IsNextDat").equals(null)) {

                    NodeList IsNextDatNode = element.getElementsByTagName("IsNextDat");
                    Element line = (Element) IsNextDatNode.item(0);

                    if (IsNextDatNode.getLength() > 0) {

                        IsNextDat = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("RouteID").equals(null)) {

                    NodeList RouteIDNode = element.getElementsByTagName("RouteID");
                    Element line = (Element) RouteIDNode.item(0);

                    if (RouteIDNode.getLength() > 0) {

                        StoreRouteID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }


                if (!element.getElementsByTagName("RouteNodeType").equals(null)) {

                    NodeList RouteNodeTypeNode = element.getElementsByTagName("RouteNodeType");
                    Element line = (Element) RouteNodeTypeNode.item(0);

                    if (RouteNodeTypeNode.getLength() > 0) {

                        RouteNodeType = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                if (!element.getElementsByTagName("StoreCatNodeId").equals(null)) {

                    NodeList StoreCatNodeIdNode = element.getElementsByTagName("StoreCatNodeId");
                    Element line = (Element) StoreCatNodeIdNode.item(0);

                    if (StoreCatNodeIdNode.getLength() > 0) {

                        StoreCatNodeId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }


                if (!element.getElementsByTagName("PaymentStage").equals(null)) {

                    NodeList PaymentStageNode = element.getElementsByTagName("PaymentStage");
                    Element line = (Element) PaymentStageNode.item(0);

                    if (PaymentStageNode.getLength() > 0) {

                        PaymentStage = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (StoreSelection.hmapStoreIdSstat != null) {
                    //StoreIDPDAFromServer

                    if (StoreSelection.hmapStoreIdSstat.containsKey(StoreIDPDAFromServer)) {
                        StoreID = StoreIDPDAFromServer;
                    }
                    if (StoreSelection.hmapStoreIdSstat.containsKey(StoreID)) {
                        Sstat = Integer.parseInt(StoreSelection.hmapStoreIdSstat.get(StoreID));
                        ForDate = StoreSelection.hmapStoreIdForDate.get(StoreID);
                        flgOrderType = Integer.parseInt(StoreSelection.hmapStoreIdflgOrderType.get(StoreID));
                    } else {
                        if (!element.getElementsByTagName("Sstat").equals(null)) {

                            NodeList SstatNode = element.getElementsByTagName("Sstat");
                            Element line = (Element) SstatNode.item(0);

                            if (SstatNode.getLength() > 0) {

                                Sstat = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                            }
                        }
                    }
                }

                if (!element.getElementsByTagName("flgHasQuote").equals(null)) {

                    NodeList flgHasQuoteNode = element.getElementsByTagName("flgHasQuote");
                    Element line = (Element) flgHasQuoteNode.item(0);

                    if (flgHasQuoteNode.getLength() > 0) {

                        flgHasQuote = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                //flgQuote

                if (!element.getElementsByTagName("flgAllowQuotation").equals(null)) {

                    NodeList flgAllowQuotationNode = element.getElementsByTagName("flgAllowQuotation");
                    Element line = (Element) flgAllowQuotationNode.item(0);

                    if (flgAllowQuotationNode.getLength() > 0) {

                        flgAllowQuotation = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }


                if (!element.getElementsByTagName("flgSubmitFromQuotation").equals(null)) {

                    NodeList flgSubmitFromQuotationNode = element.getElementsByTagName("flgSubmitFromQuotation");
                    Element line = (Element) flgSubmitFromQuotationNode.item(0);

                    if (flgSubmitFromQuotationNode.getLength() > 0) {

                        flgSubmitFromQuotation = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                if (!element.getElementsByTagName("flgGSTCapture").equals(null)) {

                    NodeList flgGSTCaptureNode = element.getElementsByTagName("flgGSTCapture");
                    Element line = (Element) flgGSTCaptureNode.item(0);

                    if (flgGSTCaptureNode.getLength() > 0) {

                        flgGSTCapture = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("flgGSTCompliance").equals(null)) {

                    NodeList flgGSTComplianceNode = element.getElementsByTagName("flgGSTCompliance");
                    Element line = (Element) flgGSTComplianceNode.item(0);

                    if (flgGSTComplianceNode.getLength() > 0) {

                        flgGSTCompliance = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("GSTNumber").equals(null)) {

                    NodeList GSTNumberNode = element.getElementsByTagName("GSTNumber");
                    Element line = (Element) GSTNumberNode.item(0);

                    if (GSTNumberNode.getLength() > 0) {

                        GSTNumber = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("CollectionReq").equals(null)) {

                    NodeList CollectionReqNode = element.getElementsByTagName("CollectionReq");
                    Element line = (Element) CollectionReqNode.item(0);

                    if (CollectionReqNode.getLength() > 0) {

                        collectionRequired = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("OwnerName").equals(null)) {
                    NodeList OwnerNameNode = element.getElementsByTagName("OwnerName");
                    Element line = (Element) OwnerNameNode.item(0);
                    if (OwnerNameNode.getLength() > 0) {
                        OwnerName = XMLParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("StoreContactNo").equals(null)) {
                    NodeList StoreContactNoNode = element.getElementsByTagName("StoreContactNo");
                    Element line = (Element) StoreContactNoNode.item(0);
                    if (StoreContactNoNode.getLength() > 0) {
                        StoreContactNo = XMLParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("StoreCatType").equals(null)) {
                    NodeList StoreCatTypeNode = element.getElementsByTagName("StoreCatType");
                    Element line = (Element) StoreCatTypeNode.item(0);
                    if (StoreCatTypeNode.getLength() > 0) {
                        StoreCatType = XMLParser.getCharacterDataFromElement(line);
                    }
                }
                //flgCaptureCompetitorPTR
                if (!element.getElementsByTagName("flgCompSurvey").equals(null)) {
                    NodeList flgCaptureCompetitorPTRNode = element.getElementsByTagName("flgCompSurvey");
                    Element line = (Element) flgCaptureCompetitorPTRNode.item(0);
                    if (flgCaptureCompetitorPTRNode.getLength() > 0) {
                        flgCaptureCompetitorPTR = XMLParser.getCharacterDataFromElement(line);
                    }
                }


                if (flgGSTCompliance.equals("1")) {
                    flgGSTRecordFromServer = 1;
                }
                if (flgGSTCapture.equals(null)) {
                    flgGSTCapture = "1";
                }
                if (flgGSTCompliance.equals(null)) {
                    flgGSTCompliance = "NA";
                }
                if (GSTNumber.equals(null)) {
                    GSTNumber = "0";
                }


                //flgSubmitFromQuotation
                //flgAllowQuotation
                int AutoIdStore = 0;
                AutoIdStore = i + 1;
                String StoreAddress = "";
                //if(!StoreSelection.hmapStoreIdSstat.containsKey(StoreID)) {
                if (!StoreIDPDAFromServer.equals(StoreID)) {
                    dbengine.saveSOAPdataStoreList(StoreID, StoreName, StoreType, StoreLatitude, StoreLongitude, LastVisitDate, LastTransactionDate, dateVAL.toString().trim(), AutoIdStore, Sstat, IsClose, IsNextDat, StoreRouteID, StoreCatNodeId, StoreAddress, PaymentStage, flgHasQuote, flgAllowQuotation, flgSubmitFromQuotation, flgGSTCapture, flgGSTCompliance, GSTNumber, flgGSTRecordFromServer, RouteNodeType, collectionRequired, flgOrderType, OwnerName, StoreContactNo, StoreCatType, flgCaptureCompetitorPTR);
                }
                //}


            }


            NodeList tblStoreListWithPaymentAddressMR = doc.getElementsByTagName("tblStoreListWithPaymentAddressMR");
            for (int i = 0; i < tblStoreListWithPaymentAddressMR.getLength(); i++) {

                String StoreID = "0";
                int OutAddTypeID = 0;

                String Address = "";
                String AddressDet = "Not Available";
                int OutAddID = 0;


                Element element = (Element) tblStoreListWithPaymentAddressMR.item(i);

                if (!element.getElementsByTagName("StoreID").equals(null)) {

                    NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                    Element line = (Element) StoreIDNode.item(0);

                    if (StoreIDNode.getLength() > 0) {

                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("OutAddTypeID").equals(null)) {

                    NodeList OutAddTypeIDNode = element.getElementsByTagName("OutAddTypeID");
                    Element line = (Element) OutAddTypeIDNode.item(0);

                    if (OutAddTypeIDNode.getLength() > 0) {

                        OutAddTypeID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("Address").equals(null)) {

                    NodeList AddressNode = element.getElementsByTagName("Address");
                    Element line = (Element) AddressNode.item(0);

                    if (AddressNode.getLength() > 0) {

                        Address = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("AddressDet").equals(null)) {

                    NodeList AddressDetNode = element.getElementsByTagName("AddressDet");
                    Element line = (Element) AddressDetNode.item(0);

                    if (AddressDetNode.getLength() > 0) {

                        AddressDet = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("OutAddID").equals(null)) {

                    NodeList OutAddIDNode = element.getElementsByTagName("OutAddID");
                    Element line = (Element) OutAddIDNode.item(0);

                    if (OutAddIDNode.getLength() > 0) {

                        OutAddID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                int AutoIdStore = 0;
                AutoIdStore = i + 1;

                dbengine.saveSOAPdataStoreListAddressMap(StoreID, OutAddTypeID, Address, AddressDet, OutAddID);
            }


            NodeList tblStoreSomeProdQuotePriceMstr = doc.getElementsByTagName("tblStoreSomeProdQuotePriceMstr");
            for (int i = 0; i < tblStoreSomeProdQuotePriceMstr.getLength(); i++) {

                String prdId = "0";
                String StoreID = "0";
                String QPBT = "0";
                String QPAT = "0";
                String QPTaxAmt = "0";
                int MinDlvryQty = 0;
                String UOMID = "0";


                Element element = (Element) tblStoreSomeProdQuotePriceMstr.item(i);

                if (!element.getElementsByTagName("PrdId").equals(null)) {

                    NodeList PrdIdNode = element.getElementsByTagName("PrdId");
                    Element line = (Element) PrdIdNode.item(0);

                    if (PrdIdNode.getLength() > 0) {

                        prdId = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("StoreId").equals(null)) {

                    NodeList StoreIDNode = element.getElementsByTagName("StoreId");
                    Element line = (Element) StoreIDNode.item(0);

                    if (StoreIDNode.getLength() > 0) {

                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("QPBT").equals(null)) {

                    NodeList QPBTNode = element.getElementsByTagName("QPBT");
                    Element line = (Element) QPBTNode.item(0);

                    if (QPBTNode.getLength() > 0) {

                        QPBT = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("QPAT").equals(null)) {

                    NodeList QPATNode = element.getElementsByTagName("QPAT");
                    Element line = (Element) QPATNode.item(0);

                    if (QPATNode.getLength() > 0) {

                        QPAT = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("QPTaxAmt").equals(null)) {

                    NodeList QPTaxmtNode = element.getElementsByTagName("QPTaxAmt");
                    Element line = (Element) QPTaxmtNode.item(0);

                    if (QPTaxmtNode.getLength() > 0) {

                        QPTaxAmt = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("MinDlvryQty").equals(null)) {

                    NodeList AddressDetNode = element.getElementsByTagName("MinDlvryQty");
                    Element line = (Element) AddressDetNode.item(0);

                    if (AddressDetNode.getLength() > 0) {

                        MinDlvryQty = Integer.valueOf(xmlParser.getCharacterDataFromElement(line));
                    }
                }


                if (!element.getElementsByTagName("UOMID").equals(null)) {

                    NodeList OutAddIDNode = element.getElementsByTagName("UOMID");
                    Element line = (Element) OutAddIDNode.item(0);

                    if (OutAddIDNode.getLength() > 0) {

                        UOMID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                int AutoIdStore = 0;
                AutoIdStore = i + 1;

                dbengine.insertMinDelQty(prdId, StoreID, QPBT, QPTaxAmt, MinDlvryQty, UOMID, QPAT);
            }

            dbengine.open();
            dbengine.deletetblVideoStoreWise();
            dbengine.close();


            NodeList tblVideoStoreWiseMstrMstr = doc.getElementsByTagName("tblVideoStoreWise");
            for (int i = 0; i < tblVideoStoreWiseMstrMstr.getLength(); i++) {

                String StoreID = "0";
                String VedioName = "0";
                String VideoURL = "0";
                String flagVideoShow = "0";
                String flgNewVedioLaunched = "0";
                String VideoDownloadPath = "0";
                String VideoViewed = "0";
                String ChannelID = "0";
                String ContentID = "0";
                String ContentType = "0";
                String Versionupdatedtime = "0";
                String ViewDateTime = "0";
                String FullVideoPlayed = "0";


                Element element = (Element) tblVideoStoreWiseMstrMstr.item(i);

                if (!element.getElementsByTagName("StoreID").equals(null)) {

                    NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                    Element line = (Element) StoreIDNode.item(0);

                    if (StoreIDNode.getLength() > 0) {

                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("Contentname").equals(null)) {

                    NodeList VedioNameNode = element.getElementsByTagName("Contentname");
                    Element line = (Element) VedioNameNode.item(0);

                    if (VedioNameNode.getLength() > 0) {

                        VedioName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ContentURL").equals(null)) {

                    NodeList VideoURLNode = element.getElementsByTagName("ContentURL");
                    Element line = (Element) VideoURLNode.item(0);

                    if (VideoURLNode.getLength() > 0) {

                        VideoURL = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("flagVideoShow").equals(null)) {

                    NodeList flagVideoShowNode = element.getElementsByTagName("flagVideoShow");
                    Element line = (Element) flagVideoShowNode.item(0);

                    if (flagVideoShowNode.getLength() > 0) {

                        flagVideoShow = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("flgNewVideoLaunched").equals(null)) {

                    NodeList flgNewVedioLaunchedNode = element.getElementsByTagName("flgNewVideoLaunched");
                    Element line = (Element) flgNewVedioLaunchedNode.item(0);

                    if (flgNewVedioLaunchedNode.getLength() > 0) {

                        flgNewVedioLaunched = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ChannelID").equals(null)) {

                    NodeList flgNewVedioLaunchedNode = element.getElementsByTagName("ChannelID");
                    Element line = (Element) flgNewVedioLaunchedNode.item(0);

                    if (flgNewVedioLaunchedNode.getLength() > 0) {

                        ChannelID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ContentID").equals(null)) {

                    NodeList flgNewVedioLaunchedNode = element.getElementsByTagName("ContentID");
                    Element line = (Element) flgNewVedioLaunchedNode.item(0);

                    if (flgNewVedioLaunchedNode.getLength() > 0) {

                        ContentID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ContentType").equals(null)) {

                    NodeList flgNewVedioLaunchedNode = element.getElementsByTagName("ContentType");
                    Element line = (Element) flgNewVedioLaunchedNode.item(0);

                    if (flgNewVedioLaunchedNode.getLength() > 0) {

                        ContentType = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("Versionupdatedtime").equals(null)) {

                    NodeList flgNewVedioLaunchedNode = element.getElementsByTagName("Versionupdatedtime");
                    Element line = (Element) flgNewVedioLaunchedNode.item(0);

                    if (flgNewVedioLaunchedNode.getLength() > 0) {

                        Versionupdatedtime = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.savetblVideoStoreWise(StoreID, flagVideoShow, VideoURL, VideoDownloadPath, VedioName, flgNewVedioLaunched, VideoViewed, ChannelID, ContentID, ContentType, Versionupdatedtime, ViewDateTime, FullVideoPlayed);
            }

            //competitor Product master

            NodeList tblCompetitorPrdctMstr = doc.getElementsByTagName("tblCompetitorPrdctMstr");
            for (int i = 0; i < tblCompetitorPrdctMstr.getLength(); i++) {

                String CompetitionProductID = "0";
                String CompetitionProductName = "0";
                String CompetitorBrandID = "0";
                String LTFoodsSimilarBrand = "0";
                int CategoryID = 0;
                int Seq = 0;
                String Category = "0";
                int BusinessUnitId = 0;
                String BusinessUnit = "0";
                String ProductImg = "";
                String flgActive = "0";
                String Unit_In_gram = "1";
                String MinRate = "0";
                String MaxRate = "0";
                String photoPath = "";

                Element element = (Element) tblCompetitorPrdctMstr.item(i);

                if (!element.getElementsByTagName("CompetitionProductID").equals(null)) {

                    NodeList CompetitionProductIDNode = element.getElementsByTagName("CompetitionProductID");
                    Element line = (Element) CompetitionProductIDNode.item(0);

                    if (CompetitionProductIDNode.getLength() > 0) {

                        CompetitionProductID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("CompetitionProductName").equals(null)) {

                    NodeList CompetitionProductNameNode = element.getElementsByTagName("CompetitionProductName");
                    Element line = (Element) CompetitionProductNameNode.item(0);

                    if (CompetitionProductNameNode.getLength() > 0) {

                        CompetitionProductName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("CompetitorBrandID").equals(null)) {

                    NodeList CompetitorBrandIDNode = element.getElementsByTagName("CompetitorBrandID");
                    Element line = (Element) CompetitorBrandIDNode.item(0);

                    if (CompetitorBrandIDNode.getLength() > 0) {

                        CompetitorBrandID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("LTFoodsSimilarBrand").equals(null)) {

                    NodeList LTFoodsSimilarBrandNode = element.getElementsByTagName("LTFoodsSimilarBrand");
                    Element line = (Element) LTFoodsSimilarBrandNode.item(0);

                    if (LTFoodsSimilarBrandNode.getLength() > 0) {

                        LTFoodsSimilarBrand = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("CategoryID").equals(null)) {

                    NodeList CategoryIDNode = element.getElementsByTagName("CategoryID");
                    Element line = (Element) CategoryIDNode.item(0);

                    if (CategoryIDNode.getLength() > 0) {

                        CategoryID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("Seq").equals(null)) {

                    NodeList SeqNode = element.getElementsByTagName("Seq");
                    Element line = (Element) SeqNode.item(0);

                    if (SeqNode.getLength() > 0) {

                        Seq = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("Category").equals(null)) {

                    NodeList CategoryNode = element.getElementsByTagName("Category");
                    Element line = (Element) CategoryNode.item(0);

                    if (CategoryNode.getLength() > 0) {

                        Category = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("BusinessUnitId").equals(null)) {

                    NodeList BusinessUnitIdNode = element.getElementsByTagName("BusinessUnitId");
                    Element line = (Element) BusinessUnitIdNode.item(0);

                    if (BusinessUnitIdNode.getLength() > 0) {

                        BusinessUnitId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                if (!element.getElementsByTagName("BusinessUnit").equals(null)) {

                    NodeList BusinessUnitNode = element.getElementsByTagName("BusinessUnit");
                    Element line = (Element) BusinessUnitNode.item(0);

                    if (BusinessUnitNode.getLength() > 0) {

                        BusinessUnit = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ProductImg").equals(null)) {

                    NodeList ProductImgNode = element.getElementsByTagName("ProductImg");
                    Element line = (Element) ProductImgNode.item(0);

                    if (ProductImgNode.getLength() > 0) {

                        ProductImg = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("flgActive").equals(null)) {

                    NodeList flgActiveNode = element.getElementsByTagName("flgActive");
                    Element line = (Element) flgActiveNode.item(0);

                    if (flgActiveNode.getLength() > 0) {

                        flgActive = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                //String ,String ,String ,String photoPath
                if (!element.getElementsByTagName("Unit_In_gram").equals(null)) {

                    NodeList Unit_In_gramNode = element.getElementsByTagName("Unit_In_gram");
                    Element line = (Element) Unit_In_gramNode.item(0);

                    if (Unit_In_gramNode.getLength() > 0) {

                        Unit_In_gram = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("MinRate").equals(null)) {

                    NodeList MinRateNode = element.getElementsByTagName("MinRate");
                    Element line = (Element) MinRateNode.item(0);

                    if (MinRateNode.getLength() > 0) {

                        MinRate = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("MaxRate").equals(null)) {

                    NodeList MaxRateNode = element.getElementsByTagName("MaxRate");
                    Element line = (Element) MaxRateNode.item(0);

                    if (MaxRateNode.getLength() > 0) {

                        MaxRate = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("ProductImgPath").equals(null)) {

                    NodeList ProductImgPathNode = element.getElementsByTagName("ProductImgPath");
                    Element line = (Element) ProductImgPathNode.item(0);

                    if (ProductImgPathNode.getLength() > 0) {

                        String ProductImgPath = xmlParser.getCharacterDataFromElement(line);
                        photoPath = downLoadingCmpttrImage(ProductImgPath, ProductImg);
                    }
                }

//betudubey
                //if(TextUtils.isEmpty(photoPath))

                dbengine.insertCmpttrPrdctMstr(CompetitionProductID, CompetitionProductName, CompetitorBrandID, LTFoodsSimilarBrand, CategoryID, Seq, Category, BusinessUnitId, BusinessUnit, ProductImg, flgActive, Unit_In_gram, MinRate, MaxRate, photoPath);
            }


            setmovie.director = "1";
            // System.out.println("ServiceWorkerNitish getallStores Completed ");
            flagExecutedServiceSuccesfully = 1;
            return setmovie;


        } catch (Exception e) {

            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public String downLoadingCmpttrImage(String SelfieNameURL, String SelfieName) {
        String URL_String = SelfieNameURL;
        String Video_Name = SelfieName;
        String PATH = "";
        PATH = Environment.getExternalStorageDirectory() + "/" + CommonInfo.CompetitorImagesFolder + "/" + SelfieName;
        File fdelete = new File(PATH);
        if (fdelete.exists()) {

        } else {
            try {
                PATH = "";
                URL url = new URL(URL_String);
                URLConnection connection = url.openConnection();
                HttpURLConnection urlConnection = (HttpURLConnection) connection;
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();
                PATH = Environment.getExternalStorageDirectory() + "/" + CommonInfo.CompetitorImagesFolder + "/";

                File file2 = new File(PATH + Video_Name);
                if (file2.exists()) {
                    file2.delete();
                }

                File file1 = new File(PATH);
                if (!file1.exists()) {
                    file1.mkdirs();
                }


                File file = new File(file1, Video_Name);

                int size = connection.getContentLength();


                FileOutputStream fileOutput = new FileOutputStream(file);

                InputStream inputStream = urlConnection.getInputStream();

                byte[] buffer = new byte[size];
                int bufferLength = 0;
                long total = 0;
                int current = 0;
                while ((bufferLength = inputStream.read(buffer)) != -1) {
                    total += bufferLength;

                    fileOutput.write(buffer, 0, bufferLength);
                }

                fileOutput.close();
                PATH += Video_Name;

            } catch (Exception e) {
                PATH = "";
            }

        }


        return PATH;
    }

	/*public ServiceWorker getallStoresVideoDetails(Context ctx, String dateVAL, String uuid, String rID)
	{
		this.context = ctx;

		DBAdapterKenya dbengine = new DBAdapterKenya(context);
		String RouteType="0";
		try
		{
			dbengine.open();
			String RouteID=dbengine.GetActiveRouteID();
			RouteType=dbengine.FetchRouteType(rID);
			dbengine.close();
			System.out.println("hi"+RouteType);
		}
		catch(Exception e)
		{
			System.out.println("error"+e);
		}


		final String SOAP_ACTION = "http://tempuri.org/fnGetStoreWiseVedios";
		final String METHOD_NAME = "fnGetStoreWiseVedios";
		final String NAMESPACE = "http://tempuri.org/";
		final String URL = UrlForWebService;

		SoapObject table = null; // Contains table of dataset that returned
		// through SoapObject
		SoapObject client = null; // Its the client petition to the web service
		SoapObject tableRow = null; // Contains row of table
		SoapObject responseBody = null; // Contains XML content of dataset

		//SoapObject param
		HttpTransportSE transport = null; // That call webservice
		SoapSerializationEnvelope sse = null;

		sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
		// Note if class name isn't "ABC_CLASS_NAME" ,you must change
		sse.dotNet = true; // if WebService written .Net is result=true
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				URL,timeout);

		ServiceWorker setmovie = new ServiceWorker();
		try {
			client = new SoapObject(NAMESPACE, METHOD_NAME);

			Date currDate= new Date();
			SimpleDateFormat currDateFormat =new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);

			currSysDate = currDateFormat.format(currDate).toString();
			SysDate = currSysDate.trim().toString();

			client.addProperty("bydate", dateVAL.toString());
			client.addProperty("IMEINo", uuid.toString());
			client.addProperty("rID", rID.toString());
			client.addProperty("RouteType", RouteType);
			client.addProperty("SysDate", SysDate.toString());
			client.addProperty("AppVersionID", dbengine.AppVersionID.toString());
			client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);



			sse.setOutputSoapObject(client);
			sse.bodyOut = client;
			System.out.println("S1");

			androidHttpTransport.call(SOAP_ACTION, sse);
			responseBody = (SoapObject)sse.bodyIn;


			int totalCount = responseBody.getPropertyCount();

			// System.out.println("Kajol 2 :"+totalCount);
			String resultString=androidHttpTransport.responseDump;

			String name=responseBody.getProperty(0).toString();

			// System.out.println("Kajol 3 :"+name);

			XMLParser xmlParser = new XMLParser();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(name));
			Document doc = db.parse(is);


            dbengine.open();
			dbengine.deletetblVideoStoreWise();
			dbengine.close();





			NodeList tblStoreSomeProdQuotePriceMstr = doc.getElementsByTagName("tblVideoStoreWise");
			for (int i = 0; i < tblStoreSomeProdQuotePriceMstr.getLength(); i++)
			{

				String StoreID="0";
				String VedioName="0";
				String VideoURL="0";
				String flagVideoShow="0";
				String flgNewVedioLaunched="0";
				String VideoDownloadPath="0";
				String VideoViewed="0";





				Element element = (Element) tblStoreSomeProdQuotePriceMstr.item(i);

				if(!element.getElementsByTagName("StoreID").equals(null))
				{

					NodeList StoreIDNode = element.getElementsByTagName("StoreID");
					Element     line = (Element) StoreIDNode.item(0);

					if(StoreIDNode.getLength()>0)
					{

						StoreID=xmlParser.getCharacterDataFromElement(line);
					}
				}
				if(!element.getElementsByTagName("VedioName").equals(null))
				{

					NodeList VedioNameNode = element.getElementsByTagName("VedioName");
					Element     line = (Element) VedioNameNode.item(0);

					if(VedioNameNode.getLength()>0)
					{

						VedioName=xmlParser.getCharacterDataFromElement(line);
					}
				}

				if(!element.getElementsByTagName("VideoURL").equals(null))
				{

					NodeList VideoURLNode = element.getElementsByTagName("VideoURL");
					Element     line = (Element) VideoURLNode.item(0);

					if(VideoURLNode.getLength()>0)
					{

						VideoURL=xmlParser.getCharacterDataFromElement(line);
					}
				}

				if(!element.getElementsByTagName("flagVideoShow").equals(null))
				{

					NodeList flagVideoShowNode = element.getElementsByTagName("flagVideoShow");
					Element     line = (Element) flagVideoShowNode.item(0);

					if(flagVideoShowNode.getLength()>0)
					{

						flagVideoShow=xmlParser.getCharacterDataFromElement(line);
					}
				}

				if(!element.getElementsByTagName("flgNewVedioLaunched").equals(null))
				{

					NodeList flgNewVedioLaunchedNode = element.getElementsByTagName("flgNewVedioLaunched");
					Element     line = (Element) flgNewVedioLaunchedNode.item(0);

					if(flgNewVedioLaunchedNode.getLength()>0)
					{

						flgNewVedioLaunched=xmlParser.getCharacterDataFromElement(line);
					}
				}




				dbengine.savetblVideoStoreWise(StoreID,flagVideoShow,VideoURL,VideoDownloadPath,VedioName,flgNewVedioLaunched,VideoViewed);
			}


			setmovie.director = "1";
			// System.out.println("ServiceWorkerNitish getallStores Completed ");
			flagExecutedServiceSuccesfully=1;
			return setmovie;


		} catch (Exception e) {

			setmovie.director = e.toString();
			setmovie.movie_name = e.toString();
			flagExecutedServiceSuccesfully=0;
			//dbengine.close();
			return setmovie;
		}

	}*/


    public ServiceWorker getAvailableAndUpdatedVersionOfAppNew(Context ctx, String uuid, String CurDate, int DatabaseVersion, int ApplicationID) {

        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/GetIMEIVersionDetailStatusNew";
        final String METHOD_NAME = "GetIMEIVersionDetailStatusNew";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        //Create request
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();


        // // System.out.println("Kajol 100");

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            // // System.out.println("Kajol 101");
            client.addProperty("uuid", uuid.toString());
            client.addProperty("DatabaseVersion", DatabaseVersion);
            client.addProperty("ApplicationID", ApplicationID);

            // // System.out.println("Kajol 102");
            sse.setOutputSoapObject(client);
            // // System.out.println("Kajol 103");
            sse.bodyOut = client;
            // // System.out.println("Kajol 104");

            androidHttpTransport.call(SOAP_ACTION, sse);

            // // System.out.println("Kajol 1");

            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            // // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            dbengine.droptblUserAuthenticationMstrTBL();
            dbengine.createtblUserAuthenticationMstrTBL();
            dbengine.dropAvailbUpdatedVersionTBL();
            dbengine.createAvailbUpdatedVersionTBL();
            dbengine.droptblManagerMstr();
            dbengine.createtblManagerMstr();


            NodeList tblSchemeStoreMappingNode = doc.getElementsByTagName("tblUserAuthentication");

            for (int i = 0; i < tblSchemeStoreMappingNode.getLength(); i++) {
                String flgUserAuthenticated = "0";
                int flgAllRoutesData = 0;
                String flgAppStatus = "0";
                String DisplayMessage = "No Message";
                String flgValidApplication = "0";
                String MessageForInvalid = "No Message";
                String PersonName = "0";
                String FlgRegistered = "0";
                int flgISR = 0;
                String PersonNodeID = "0";
                String PersonNodeType = "0";
                String SalesAreaName = "0";
                String ContactNumber = "0";
                String flgPersonTodaysAtt = "0";


                Element element = (Element) tblSchemeStoreMappingNode.item(i);

                NodeList StoreIDNode = element.getElementsByTagName("flgUserAuthenticated");
                Element line = (Element) StoreIDNode.item(0);
                flgUserAuthenticated = xmlParser.getCharacterDataFromElement(line);


                if (!element.getElementsByTagName("PersonName").equals(null)) {

                    NodeList PersonNameNode = element.getElementsByTagName("PersonName");
                    Element line1 = (Element) PersonNameNode.item(0);

                    if (PersonNameNode.getLength() > 0) {

                        PersonName = xmlParser.getCharacterDataFromElement(line1);
                    }
                }
                if (!element.getElementsByTagName("FlgRegistered").equals(null)) {

                    NodeList FlgRegisteredNode = element.getElementsByTagName("FlgRegistered");
                    Element line2 = (Element) FlgRegisteredNode.item(0);

                    if (FlgRegisteredNode.getLength() > 0) {

                        FlgRegistered = xmlParser.getCharacterDataFromElement(line2);
                    }
                }

                if (!element.getElementsByTagName("ContactNumber").equals(null)) {

                    NodeList ContactNumberNode = element.getElementsByTagName("ContactNumber");
                    Element line2 = (Element) ContactNumberNode.item(0);

                    if (ContactNumberNode.getLength() > 0) {

                        ContactNumber = xmlParser.getCharacterDataFromElement(line2);
                    }
                }
                if (!element.getElementsByTagName("PersonNodeID").equals(null)) {

                    NodeList PersonNodeIDNode = element.getElementsByTagName("PersonNodeID");
                    Element line2 = (Element) PersonNodeIDNode.item(0);

                    if (PersonNodeIDNode.getLength() > 0) {

                        PersonNodeID = xmlParser.getCharacterDataFromElement(line2);
                    }
                }
                if (!element.getElementsByTagName("PersonNodeType").equals(null)) {

                    NodeList PersonNodeTypeNode = element.getElementsByTagName("PersonNodeType");
                    Element line2 = (Element) PersonNodeTypeNode.item(0);

                    if (PersonNodeTypeNode.getLength() > 0) {

                        PersonNodeType = xmlParser.getCharacterDataFromElement(line2);
                    }
                }
                if (!element.getElementsByTagName("SalesAreaName").equals(null)) {

                    NodeList SalesAreaNameNode = element.getElementsByTagName("SalesAreaName");
                    Element line2 = (Element) SalesAreaNameNode.item(0);

                    if (SalesAreaNameNode.getLength() > 0) {

                        SalesAreaName = xmlParser.getCharacterDataFromElement(line2);
                    }
                }


                // System.out.println("Kajol tblUserAuthentication StoreID: " +flgUserAuthenticated );
                NodeList flgAllRoutesDataNode = element.getElementsByTagName("flgToShowAllRoutesData");
                Element line1 = (Element) flgAllRoutesDataNode.item(0);
                flgAllRoutesData = Integer.parseInt(xmlParser.getCharacterDataFromElement(line1));


                NodeList flgAppStatusNode = element.getElementsByTagName("flgAppStatus");
                line = (Element) flgAppStatusNode.item(0);
                if (flgAppStatusNode.getLength() > 0) {
                    flgAppStatus = xmlParser.getCharacterDataFromElement(line);
                }
                NodeList DisplayMessageNode = element.getElementsByTagName("DisplayMessage");
                line = (Element) DisplayMessageNode.item(0);
                if (DisplayMessageNode.getLength() > 0) {
                    DisplayMessage = xmlParser.getCharacterDataFromElement(line);
                }
                NodeList flgValidApplicationNode = element.getElementsByTagName("flgValidApplication");
                line = (Element) flgValidApplicationNode.item(0);
                if (flgValidApplicationNode.getLength() > 0) {
                    flgValidApplication = xmlParser.getCharacterDataFromElement(line);
                }
                NodeList MessageForInvalidNode = element.getElementsByTagName("MessageForInvalid");
                line = (Element) MessageForInvalidNode.item(0);
                if (MessageForInvalidNode.getLength() > 0) {
                    MessageForInvalid = xmlParser.getCharacterDataFromElement(line);
                }

                if (!element.getElementsByTagName("flgISR").equals(null)) {

                    NodeList flgISRNode = element.getElementsByTagName("flgISR");
                    Element line2 = (Element) flgISRNode.item(0);

                    if (flgISRNode.getLength() > 0) {

                        flgISR = Integer.parseInt(xmlParser.getCharacterDataFromElement(line2));
                    }
                }

                NodeList flgPersonTodaysAttNode = element.getElementsByTagName("flgPersonTodaysAtt");
                line = (Element) flgPersonTodaysAttNode.item(0);
                if (flgPersonTodaysAttNode.getLength() > 0) {
                    flgPersonTodaysAtt = xmlParser.getCharacterDataFromElement(line);
                }
                dbengine.savetblUserAuthenticationMstr(flgUserAuthenticated, flgAllRoutesData, flgAppStatus, DisplayMessage, flgValidApplication, MessageForInvalid, PersonName, FlgRegistered, flgISR, PersonNodeID, PersonNodeType, SalesAreaName, ContactNumber, flgPersonTodaysAtt);

            }


            NodeList tblSchemeMstrNode = doc.getElementsByTagName("tblAvailableVersion");
            for (int i = 0; i < tblSchemeMstrNode.getLength(); i++) {


                String VersionID = "0";
                String VersionSerialNo = "NA";
                String VersionDownloadStatus = "NA";
                Date pdaDate = new Date();
                SimpleDateFormat sdfPDaDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                String fDatePda = sdfPDaDate.format(pdaDate).toString().trim();
                String ServerDate = fDatePda;

                Element element = (Element) tblSchemeMstrNode.item(i);
                NodeList SchemeIDNode = element.getElementsByTagName("VersionID");
                Element line = (Element) SchemeIDNode.item(0);
                VersionID = xmlParser.getCharacterDataFromElement(line);
                // System.out.println("Kajol tblSchemeMstr: VersionID" +VersionID );


                NodeList SchemeNameNode = element.getElementsByTagName("VersionSerialNo");
                line = (Element) SchemeNameNode.item(0);
                VersionSerialNo = xmlParser.getCharacterDataFromElement(line);
                // System.out.println("Kajol tblSchemeMstr: VersionSerialNo " +VersionSerialNo );


                NodeList SchemeApplicationIDNode = element.getElementsByTagName("VersionDownloadStatus");
                line = (Element) SchemeApplicationIDNode.item(0);
                VersionDownloadStatus = xmlParser.getCharacterDataFromElement(line);
                // System.out.println("Kajol tblSchemeMstr: VersionDownloadStatus " +VersionDownloadStatus );

                NodeList SchemeAppliedRuleNode = element.getElementsByTagName("ServerDate");
                line = (Element) SchemeAppliedRuleNode.item(0);
                ServerDate = xmlParser.getCharacterDataFromElement(line);
                // System.out.println("Kajol tblSchemeMstr: ServerDate " +ServerDate );


                dbengine.savetblAvailbUpdatedVersion(VersionID.trim(), VersionSerialNo.trim(), VersionDownloadStatus.trim(), ServerDate);


            }


            NodeList tblManagerMstrNode = doc.getElementsByTagName("tblManagerMstr");
            for (int i = 0; i < tblManagerMstrNode.getLength(); i++) {


                String PersonID = "NA";
                String PersonType = "NA";
                String PersonName = "NA";
                String ManagerID = "NA";
                String ManagerType = "NA";
                String ManagerName = "NA";


                Element element = (Element) tblManagerMstrNode.item(i);


                NodeList PersonIDNode = element.getElementsByTagName("PersonID");
                Element line = (Element) PersonIDNode.item(0);
                if (PersonIDNode.getLength() > 0) {
                    PersonID = xmlParser.getCharacterDataFromElement(line);
                }


                NodeList PersonTypeNode = element.getElementsByTagName("PersonType");
                line = (Element) PersonTypeNode.item(0);
                if (PersonTypeNode.getLength() > 0) {
                    PersonType = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList PersonNameNode = element.getElementsByTagName("PersonName");
                line = (Element) PersonNameNode.item(0);
                if (PersonNameNode.getLength() > 0) {
                    PersonName = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList ManagerIDNode = element.getElementsByTagName("ManagerID");
                line = (Element) ManagerIDNode.item(0);
                if (ManagerIDNode.getLength() > 0) {
                    ManagerID = xmlParser.getCharacterDataFromElement(line);
                }


                NodeList ManagerTypeNode = element.getElementsByTagName("ManagerType");
                line = (Element) ManagerTypeNode.item(0);
                if (ManagerTypeNode.getLength() > 0) {
                    ManagerType = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList ManagerNameNode = element.getElementsByTagName("ManagerName");
                line = (Element) ManagerNameNode.item(0);
                if (ManagerNameNode.getLength() > 0) {
                    ManagerName = xmlParser.getCharacterDataFromElement(line);
                }


                dbengine.savetblManagerMstr(PersonID.trim(), PersonType.trim(), PersonName.trim(), ManagerID.trim(), ManagerType.trim(), ManagerName.trim());


            }


            setmovie.director = "1";
            dbengine.close();
            return setmovie;

        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetIMEIVersionDetailStatusNew :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }


    }

/*
public ServiceWorker getAvailableAndUpdatedVersionOfAppNew(Context ctx,String uuid,String CurDate,int DatabaseVersion,int ApplicationID)
{

	this.context = ctx;
	DBAdapterKenya dbengine = new DBAdapterKenya(context);
	dbengine.open();

	decimalFormat.applyPattern(pattern);

	int chkTblStoreListContainsRow=1;
	StringReader read;
	InputSource inputstream;
	final String SOAP_ACTION = "http://tempuri.org/GetIMEIVersionDetailStatusNewTest";
	final String METHOD_NAME = "GetIMEIVersionDetailStatusNewTest";
	final String NAMESPACE = "http://tempuri.org/";
	final String URL = UrlForWebService;
	//Create request
	SoapObject table = null; // Contains table of dataset that returned
	// through SoapObject
	SoapObject client = null; // Its the client petition to the web service
	SoapObject tableRow = null; // Contains row of table
	SoapObject responseBody = null; // Contains XML content of dataset

	//SoapObject param
	HttpTransportSE transport = null; // That call webservice
	SoapSerializationEnvelope sse = null;

	sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

	sse.dotNet = true;
	HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,timeout);

	ServiceWorker setmovie = new ServiceWorker();


	// // System.out.println("Kajol 100");

	try {
		client = new SoapObject(NAMESPACE, METHOD_NAME);



		// // System.out.println("Kajol 101");
		client.addProperty("uuid", uuid.toString());
		client.addProperty("DatabaseVersion", DatabaseVersion);
		client.addProperty("ApplicationID", ApplicationID);

		// // System.out.println("Kajol 102");
		sse.setOutputSoapObject(client);
		// // System.out.println("Kajol 103");
		sse.bodyOut = client;
		// // System.out.println("Kajol 104");

		androidHttpTransport.call(SOAP_ACTION, sse);

		// // System.out.println("Kajol 1");

		responseBody = (SoapObject)sse.bodyIn;
		// This step: get file XML
		//responseBody = (SoapObject) sse.getResponse();
		int totalCount = responseBody.getPropertyCount();

		// // System.out.println("Kajol 2 :"+totalCount);
		String resultString=androidHttpTransport.responseDump;

		String name=responseBody.getProperty(0).toString();

		// // System.out.println("Kajol 3 :"+name);

		XMLParser xmlParser = new XMLParser();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(name));
		Document doc = db.parse(is);


		dbengine.droptblUserAuthenticationMstrTBL();
		dbengine.createtblUserAuthenticationMstrTBL();
		dbengine.dropAvailbUpdatedVersionTBL();
		dbengine.createAvailbUpdatedVersionTBL();
		dbengine.droptblManagerMstr();
		dbengine.createtblManagerMstr();


		NodeList tblSchemeStoreMappingNode = doc.getElementsByTagName("tblUserAuthentication");

		for (int i = 0; i < tblSchemeStoreMappingNode.getLength(); i++)
		{
			String flgUserAuthenticated="0";
			int flgAllRoutesData=0;
			String flgAppStatus="0";
			String DisplayMessage="No Message";
			String flgValidApplication="0";
			String	MessageForInvalid="No Message";

			Element element = (Element) tblSchemeStoreMappingNode.item(i);

			NodeList StoreIDNode = element.getElementsByTagName("flgUserAuthenticated");
			Element line = (Element) StoreIDNode.item(0);
			flgUserAuthenticated=xmlParser.getCharacterDataFromElement(line);
			// System.out.println("Kajol tblUserAuthentication StoreID: " +flgUserAuthenticated );
			NodeList flgAllRoutesDataNode = element.getElementsByTagName("flgToShowAllRoutesData");
			Element line1 = (Element) flgAllRoutesDataNode.item(0);
			flgAllRoutesData=Integer.parseInt(xmlParser.getCharacterDataFromElement(line1));


			NodeList flgAppStatusNode = element.getElementsByTagName("flgAppStatus");
			line = (Element) flgAppStatusNode.item(0);
			if(flgAppStatusNode.getLength()>0)
			{
				flgAppStatus=xmlParser.getCharacterDataFromElement(line);
			}
			NodeList DisplayMessageNode = element.getElementsByTagName("DisplayMessage");
			line = (Element) DisplayMessageNode.item(0);
			if(DisplayMessageNode.getLength()>0)
			{
				DisplayMessage=xmlParser.getCharacterDataFromElement(line);
			}
			NodeList flgValidApplicationNode = element.getElementsByTagName("flgValidApplication");
			line = (Element) flgValidApplicationNode.item(0);
			if(flgValidApplicationNode.getLength()>0)
			{
				flgValidApplication=xmlParser.getCharacterDataFromElement(line);
			}
			NodeList MessageForInvalidNode = element.getElementsByTagName("MessageForInvalid");
			line = (Element) MessageForInvalidNode.item(0);
			if(MessageForInvalidNode.getLength()>0)
			{
				MessageForInvalid=xmlParser.getCharacterDataFromElement(line);
			}

			dbengine.savetblUserAuthenticationMstr(flgUserAuthenticated,flgAllRoutesData,flgAppStatus,DisplayMessage,flgValidApplication,MessageForInvalid);

		}


		NodeList tblSchemeMstrNode = doc.getElementsByTagName("tblAvailableVersion");
		for (int i = 0; i < tblSchemeMstrNode.getLength(); i++)
		{


			String VersionID = "0";
			String VersionSerialNo= "NA";
			String VersionDownloadStatus= "NA";
			Date pdaDate=new Date();
			SimpleDateFormat sdfPDaDate = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
			String fDatePda = sdfPDaDate.format(pdaDate).toString().trim();
			String ServerDate= fDatePda;

			Element element = (Element) tblSchemeMstrNode.item(i);
			NodeList SchemeIDNode = element.getElementsByTagName("VersionID");
			Element line = (Element) SchemeIDNode.item(0);
			VersionID=xmlParser.getCharacterDataFromElement(line);
			// System.out.println("Kajol tblSchemeMstr: VersionID" +VersionID );


			NodeList SchemeNameNode = element.getElementsByTagName("VersionSerialNo");
			line = (Element) SchemeNameNode.item(0);
			VersionSerialNo=xmlParser.getCharacterDataFromElement(line);
			// System.out.println("Kajol tblSchemeMstr: VersionSerialNo " +VersionSerialNo );



			NodeList SchemeApplicationIDNode = element.getElementsByTagName("VersionDownloadStatus");
			line = (Element) SchemeApplicationIDNode.item(0);
			VersionDownloadStatus=xmlParser.getCharacterDataFromElement(line);
			// System.out.println("Kajol tblSchemeMstr: VersionDownloadStatus " +VersionDownloadStatus );

			NodeList SchemeAppliedRuleNode = element.getElementsByTagName("ServerDate");
			line = (Element) SchemeAppliedRuleNode.item(0);
			ServerDate=xmlParser.getCharacterDataFromElement(line);
			// System.out.println("Kajol tblSchemeMstr: ServerDate " +ServerDate );


			dbengine.savetblAvailbUpdatedVersion(VersionID.trim(), VersionSerialNo.trim(),VersionDownloadStatus.trim(),ServerDate);






		}


		NodeList tblManagerMstrNode = doc.getElementsByTagName("tblManagerMstr");
		for (int i = 0; i < tblManagerMstrNode.getLength(); i++)
		{


			String PersonID="NA";
			String PersonType="NA";
			String PersonName="NA";
			String  ManagerID ="NA";
			String  ManagerType="NA";
			String ManagerName ="NA";




			Element element = (Element) tblManagerMstrNode.item(i);


			NodeList PersonIDNode = element.getElementsByTagName("PersonID");
			Element line = (Element) PersonIDNode.item(0);
			if(PersonIDNode.getLength()>0)
			{
				PersonID=xmlParser.getCharacterDataFromElement(line);
			}


			NodeList PersonTypeNode = element.getElementsByTagName("PersonType");
			line = (Element) PersonTypeNode.item(0);
			if(PersonTypeNode.getLength()>0)
			{
				PersonType=xmlParser.getCharacterDataFromElement(line);
			}

			NodeList PersonNameNode = element.getElementsByTagName("PersonName");
			line = (Element) PersonNameNode.item(0);
			if(PersonNameNode.getLength()>0)
			{
				PersonName=xmlParser.getCharacterDataFromElement(line);
			}

			NodeList ManagerIDNode = element.getElementsByTagName("ManagerID");
			line = (Element) ManagerIDNode.item(0);
			if(ManagerIDNode.getLength()>0)
			{
				ManagerID=xmlParser.getCharacterDataFromElement(line);
			}


			NodeList ManagerTypeNode = element.getElementsByTagName("ManagerType");
			line = (Element) ManagerTypeNode.item(0);
			if(ManagerTypeNode.getLength()>0)
			{
				ManagerType=xmlParser.getCharacterDataFromElement(line);
			}

			NodeList ManagerNameNode = element.getElementsByTagName("ManagerName");
			line = (Element) ManagerNameNode.item(0);
			if(ManagerNameNode.getLength()>0)
			{
				ManagerName=xmlParser.getCharacterDataFromElement(line);
			}


			dbengine.savetblManagerMstr(PersonID.trim(),PersonType.trim(), PersonName.trim(),ManagerID.trim(),ManagerType.trim(),ManagerName.trim());






		}





		setmovie.director = "1";
		dbengine.close();
		return setmovie;

	} catch (Exception e) {

		// System.out.println("Aman Exception occur in GetIMEIVersionDetailStatusNew :"+e.toString());
		setmovie.director = e.toString();
		setmovie.movie_name = e.toString();
		dbengine.close();

		return setmovie;
	}





}
*/

    public ServiceWorker getQuotationDataFromServer(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        String RouteType = "0";
        try {
            dbengine.open();
            String RouteID = dbengine.GetActiveRouteID();
            RouteType = dbengine.FetchRouteType(rID);
            dbengine.close();
            System.out.println("hi" + RouteType);
        } catch (Exception e) {
            System.out.println("error" + e);
        }

        dbengine.open();
        dbengine.deleteAllQuotationTables();

        final String SOAP_ACTION = "http://tempuri.org/fnGetRouteQuoteData";
        final String METHOD_NAME = "fnGetRouteQuoteData";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

			/*// System.out.println("Aman Exception occur value bydate"+ dateVAL.toString());
			// System.out.println("Aman Exception occur value IMEINo"+ uuid.toString());
			// System.out.println("Aman Exception occur value rID"+ rID.toString());
			// System.out.println("Aman Exception occur value SysDate"+ SysDate.toString());
			// System.out.println("Aman Exception occur value AppVersionID"+ dbengine.AppVersionID.toString());
			*/
            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteType", RouteType);
            client.addProperty("SysDate", SysDate.toString());
            client.addProperty("AppVersionID", dbengine.AppVersionID.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            System.out.println("S1");

            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;


            int totalCount = responseBody.getPropertyCount();

            // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);
            System.out.println("shivam4");

            //   dbengine.open();

            NodeList tblUOMMstrNode = doc.getElementsByTagName("tblUOMMstr");
            for (int i = 0; i < tblUOMMstrNode.getLength(); i++) {

                String UOMID = "0";
                String UOM = "0";


                Element element = (Element) tblUOMMstrNode.item(i);

                if (!element.getElementsByTagName("UOMID").equals(null)) {

                    NodeList UOMIDNode = element.getElementsByTagName("UOMID");
                    Element line = (Element) UOMIDNode.item(0);

                    if (UOMIDNode.getLength() > 0) {

                        UOMID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("UOM").equals(null)) {

                    NodeList UOMNode = element.getElementsByTagName("UOM");
                    Element line = (Element) UOMNode.item(0);

                    if (UOMNode.getLength() > 0) {

                        UOM = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.savetbltblUOMMstr(UOMID, UOM);
            }


            NodeList tblSalesQuotePrcsMstrNode = doc.getElementsByTagName("tblSalesQuotePrcsMstr");
            for (int i = 0; i < tblSalesQuotePrcsMstrNode.getLength(); i++) {

                String SalesQuotePrcsId = "0";
                String SalesQuotePrcs = "0";


                Element element = (Element) tblSalesQuotePrcsMstrNode.item(i);

                if (!element.getElementsByTagName("SalesQuotePrcsId").equals(null)) {

                    NodeList SalesQuotePrcsIdNode = element.getElementsByTagName("SalesQuotePrcsId");
                    Element line = (Element) SalesQuotePrcsIdNode.item(0);

                    if (SalesQuotePrcsIdNode.getLength() > 0) {

                        SalesQuotePrcsId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SalesQuotePrcs").equals(null)) {

                    NodeList SalesQuotePrcsIdNodeNode = element.getElementsByTagName("SalesQuotePrcs");
                    Element line = (Element) SalesQuotePrcsIdNodeNode.item(0);

                    if (SalesQuotePrcsIdNodeNode.getLength() > 0) {

                        SalesQuotePrcs = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.saveSalesQuotePrcsMstr(SalesQuotePrcsId, SalesQuotePrcs);
            }


            NodeList tblSalesQuotePersonMeetMstrNode = doc.getElementsByTagName("tblSalesQuotePersonMeetMstr");
            for (int i = 0; i < tblSalesQuotePersonMeetMstrNode.getLength(); i++) {

                String SalesQuoteId = "0";
                String SalesQuoteCode = "0";
                String SalesQuotePrcsId = "0";
                String SalesQuotePrcs = "0";
                String StoreName = "0";
                String Remarks = "0";
                String StoreId = "0";
                String CreditLimit = "0";
                String CreditDays = "0";
                String ExpectedBusinessValue = "0";
                String SalesQuoteValidFrom = "0";
                String SalesQuoteValidTo = "0";
                String SalesQuoteDate = "0";
                String SalesQuoteType = "0";
                String ContactPerson = "0";
                String ContactPersonEmail = "0";
                String ContactPersonPhone = "0";
                String PaymentModeId = "0";
                String PaymentStageId = "0";
                String ManufacturerID = "0";
                String ManufacturerName = "0";


                Element element = (Element) tblSalesQuotePersonMeetMstrNode.item(i);

                if (!element.getElementsByTagName("SalesQuoteId").equals(null)) {

                    NodeList SalesQuoteIdNode = element.getElementsByTagName("SalesQuoteId");
                    Element line = (Element) SalesQuoteIdNode.item(0);

                    if (SalesQuoteIdNode.getLength() > 0) {

                        SalesQuoteId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SalesQuoteCode").equals(null)) {

                    NodeList SalesQuoteCodeNode = element.getElementsByTagName("SalesQuoteCode");
                    Element line = (Element) SalesQuoteCodeNode.item(0);

                    if (SalesQuoteCodeNode.getLength() > 0) {

                        SalesQuoteCode = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SalesQuotePrcsId").equals(null)) {

                    NodeList SalesQuotePrcsIdNode = element.getElementsByTagName("SalesQuotePrcsId");
                    Element line = (Element) SalesQuotePrcsIdNode.item(0);

                    if (SalesQuotePrcsIdNode.getLength() > 0) {

                        SalesQuotePrcsId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SalesQuotePrcs").equals(null)) {

                    NodeList SalesQuotePrcsNode = element.getElementsByTagName("SalesQuotePrcs");
                    Element line = (Element) SalesQuotePrcsNode.item(0);

                    if (SalesQuotePrcsNode.getLength() > 0) {

                        SalesQuotePrcs = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("StoreName").equals(null)) {

                    NodeList StoreNameNode = element.getElementsByTagName("StoreName");
                    Element line = (Element) StoreNameNode.item(0);

                    if (StoreNameNode.getLength() > 0) {

                        StoreName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Remarks").equals(null)) {

                    NodeList RemarksNode = element.getElementsByTagName("Remarks");
                    Element line = (Element) RemarksNode.item(0);

                    if (RemarksNode.getLength() > 0) {

                        Remarks = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("StoreId").equals(null)) {

                    NodeList StoreIdNode = element.getElementsByTagName("StoreId");
                    Element line = (Element) StoreIdNode.item(0);

                    if (StoreIdNode.getLength() > 0) {

                        StoreId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("CreditLimit").equals(null)) {

                    NodeList PaymentTermsNode = element.getElementsByTagName("CreditLimit");
                    Element line = (Element) PaymentTermsNode.item(0);

                    if (PaymentTermsNode.getLength() > 0) {

                        CreditLimit = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("CreditDays").equals(null)) {

                    NodeList PaymentTermsTypeNode = element.getElementsByTagName("CreditDays");
                    Element line = (Element) PaymentTermsTypeNode.item(0);

                    if (PaymentTermsTypeNode.getLength() > 0) {

                        CreditDays = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ExpectedBusinessValue").equals(null)) {

                    NodeList ExpectedBusinessValueNode = element.getElementsByTagName("ExpectedBusinessValue");
                    Element line = (Element) ExpectedBusinessValueNode.item(0);

                    if (ExpectedBusinessValueNode.getLength() > 0) {

                        ExpectedBusinessValue = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SalesQuoteValidFrom").equals(null)) {

                    NodeList SalesQuoteValidFromNode = element.getElementsByTagName("SalesQuoteValidFrom");
                    Element line = (Element) SalesQuoteValidFromNode.item(0);

                    if (SalesQuoteValidFromNode.getLength() > 0) {

                        SalesQuoteValidFrom = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SalesQuoteValidTo").equals(null)) {

                    NodeList SalesQuoteValidToNode = element.getElementsByTagName("SalesQuoteValidTo");
                    Element line = (Element) SalesQuoteValidToNode.item(0);

                    if (SalesQuoteValidToNode.getLength() > 0) {

                        SalesQuoteValidTo = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SalesQuoteDate").equals(null)) {

                    NodeList SalesQuoteDateNode = element.getElementsByTagName("SalesQuoteDate");
                    Element line = (Element) SalesQuoteDateNode.item(0);

                    if (SalesQuoteDateNode.getLength() > 0) {

                        SalesQuoteDate = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SalesQuoteType").equals(null)) {

                    NodeList SalesQuoteTypeNode = element.getElementsByTagName("SalesQuoteType");
                    Element line = (Element) SalesQuoteTypeNode.item(0);

                    if (SalesQuoteTypeNode.getLength() > 0) {

                        SalesQuoteType = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ContactPerson").equals(null)) {

                    NodeList ContactPersonNode = element.getElementsByTagName("ContactPerson");
                    Element line = (Element) ContactPersonNode.item(0);

                    if (ContactPersonNode.getLength() > 0) {

                        ContactPerson = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ContactPersonEmail").equals(null)) {

                    NodeList ContactPersonEmailNode = element.getElementsByTagName("ContactPersonEmail");
                    Element line = (Element) ContactPersonEmailNode.item(0);

                    if (ContactPersonEmailNode.getLength() > 0) {

                        ContactPersonEmail = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ContactPersonPhone").equals(null)) {

                    NodeList ContactPersonPhoneNode = element.getElementsByTagName("ContactPersonPhone");
                    Element line = (Element) ContactPersonPhoneNode.item(0);

                    if (ContactPersonPhoneNode.getLength() > 0) {

                        ContactPersonPhone = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("PaymentModeId").equals(null)) {

                    NodeList PaymentModeIdNode = element.getElementsByTagName("PaymentModeId");
                    Element line = (Element) PaymentModeIdNode.item(0);

                    if (PaymentModeIdNode.getLength() > 0) {

                        PaymentModeId = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                //we are using PymtStageId colunm of table as PaymentStage

                if (!element.getElementsByTagName("PaymentStage").equals(null)) {

                    NodeList PaymentStageIdNode = element.getElementsByTagName("PaymentStage");
                    Element line = (Element) PaymentStageIdNode.item(0);

                    if (PaymentStageIdNode.getLength() > 0) {

                        PaymentStageId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ManufacturerID").equals(null)) {

                    NodeList ManufacturerIDNode = element.getElementsByTagName("ManufacturerID");
                    Element line = (Element) ManufacturerIDNode.item(0);

                    if (ManufacturerIDNode.getLength() > 0) {

                        ManufacturerID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ManufacturerName").equals(null)) {

                    NodeList ManufacturerNameNode = element.getElementsByTagName("ManufacturerName");
                    Element line = (Element) ManufacturerNameNode.item(0);

                    if (ManufacturerNameNode.getLength() > 0) {

                        ManufacturerName = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.saveSalesQuotePersonMeetMstr(SalesQuoteId, SalesQuoteCode, SalesQuotePrcsId, SalesQuotePrcs, StoreName, Remarks, StoreId, CreditLimit, CreditDays, ExpectedBusinessValue, SalesQuoteValidFrom, SalesQuoteValidTo, SalesQuoteDate, SalesQuoteType, ContactPerson, ContactPersonEmail, ContactPersonPhone, PaymentModeId, PaymentStageId, ManufacturerID, ManufacturerName);
            }


            NodeList tblSalesQuoteProductsMstrNode = doc.getElementsByTagName("tblSalesQuoteProductsMstr");
            for (int i = 0; i < tblSalesQuoteProductsMstrNode.getLength(); i++) {

                String SalesQuoteId = "0";
                String Row_No = "0";
                String PrdId = "0";
                String StandardRate = "0";
                String StandardRateBeforeTax = "0";
                String RateOffer = "0";
                String InclusiveTax = "0";
                String ValidFrom = "0";
                String ValidTo = "0";
                String MinDlvryQty = "0";
                String UOMID = "0";
                String Remarks = "0";
                String LastTranscRate = "0.00";
                String ProductTaxRate = "0";

                Element element = (Element) tblSalesQuoteProductsMstrNode.item(i);

                if (!element.getElementsByTagName("SalesQuoteId").equals(null)) {

                    NodeList SalesQuoteIdNode = element.getElementsByTagName("SalesQuoteId");
                    Element line = (Element) SalesQuoteIdNode.item(0);

                    if (SalesQuoteIdNode.getLength() > 0) {

                        SalesQuoteId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Row_No").equals(null)) {

                    NodeList Row_NoNode = element.getElementsByTagName("Row_No");
                    Element line = (Element) Row_NoNode.item(0);

                    if (Row_NoNode.getLength() > 0) {

                        Row_No = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("PrdId").equals(null)) {

                    NodeList PrdIdNode = element.getElementsByTagName("PrdId");
                    Element line = (Element) PrdIdNode.item(0);

                    if (PrdIdNode.getLength() > 0) {

                        PrdId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("StandardRate").equals(null)) {

                    NodeList StandardRateNode = element.getElementsByTagName("StandardRate");
                    Element line = (Element) StandardRateNode.item(0);

                    if (StandardRateNode.getLength() > 0) {

                        StandardRate = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("StandardRateBeforeTax").equals(null)) {

                    NodeList StandardRateBeforeTaxNode = element.getElementsByTagName("StandardRateBeforeTax");
                    Element line = (Element) StandardRateBeforeTaxNode.item(0);

                    if (StandardRateBeforeTaxNode.getLength() > 0) {

                        StandardRateBeforeTax = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("RateOffer").equals(null)) {

                    NodeList RateOfferNode = element.getElementsByTagName("RateOffer");
                    Element line = (Element) RateOfferNode.item(0);

                    if (RateOfferNode.getLength() > 0) {

                        RateOffer = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("InclusiveTax").equals(null)) {

                    NodeList InclusiveTaxNode = element.getElementsByTagName("InclusiveTax");
                    Element line = (Element) InclusiveTaxNode.item(0);

                    if (InclusiveTaxNode.getLength() > 0) {

                        InclusiveTax = "1";//xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ValidFrom").equals(null)) {

                    NodeList ValidFromNode = element.getElementsByTagName("ValidFrom");
                    Element line = (Element) ValidFromNode.item(0);

                    if (ValidFromNode.getLength() > 0) {

                        ValidFrom = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ValidTo").equals(null)) {

                    NodeList ValidToNode = element.getElementsByTagName("ValidTo");
                    Element line = (Element) ValidToNode.item(0);

                    if (ValidToNode.getLength() > 0) {

                        ValidTo = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("MinDlvryQty").equals(null)) {

                    NodeList MinDlvryQtyNode = element.getElementsByTagName("MinDlvryQty");
                    Element line = (Element) MinDlvryQtyNode.item(0);

                    if (MinDlvryQtyNode.getLength() > 0) {

                        MinDlvryQty = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("UOMID").equals(null)) {

                    NodeList UOMIDNode = element.getElementsByTagName("UOMID");
                    Element line = (Element) UOMIDNode.item(0);

                    if (UOMIDNode.getLength() > 0) {

                        UOMID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Remarks").equals(null)) {

                    NodeList RemarksNode = element.getElementsByTagName("Remarks");
                    Element line = (Element) RemarksNode.item(0);

                    if (RemarksNode.getLength() > 0) {

                        Remarks = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("LastTranscRate").equals(null)) {

                    NodeList LastTranscRateNode = element.getElementsByTagName("LastTranscRate");
                    Element line = (Element) LastTranscRateNode.item(0);

                    if (LastTranscRateNode.getLength() > 0) {

                        LastTranscRate = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("TaxRate").equals(null)) {

                    NodeList ProductTaxRateNode = element.getElementsByTagName("TaxRate");
                    Element line = (Element) ProductTaxRateNode.item(0);

                    if (ProductTaxRateNode.getLength() > 0) {

                        ProductTaxRate = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                //ProductTaxRate
                //LastTranscRate

                dbengine.SalesQuoteProductsMstr(SalesQuoteId, Row_No, PrdId, StandardRate, StandardRateBeforeTax, RateOffer, InclusiveTax, ValidFrom, ValidTo, MinDlvryQty, UOMID, Remarks, LastTranscRate, ProductTaxRate);
            }


            NodeList tblSalesQuotePaymentModeMstrNode = doc.getElementsByTagName("tblSalesQuotePaymentModeMstr");
            for (int i = 0; i < tblSalesQuotePaymentModeMstrNode.getLength(); i++) {

                String PymtModeId = "0";
                String PymtMode = "0";


                Element element = (Element) tblSalesQuotePaymentModeMstrNode.item(i);

                if (!element.getElementsByTagName("PymtModeId").equals(null)) {

                    NodeList PymtModeIdNode = element.getElementsByTagName("PymtModeId");
                    Element line = (Element) PymtModeIdNode.item(0);

                    if (PymtModeIdNode.getLength() > 0) {

                        PymtModeId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("PymtMode").equals(null)) {

                    NodeList PymtModeNode = element.getElementsByTagName("PymtMode");
                    Element line = (Element) PymtModeNode.item(0);

                    if (PymtModeNode.getLength() > 0) {

                        PymtMode = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.SavetblSalesQuotePaymentModeMstr(PymtModeId, PymtMode);
            }


            NodeList tblSalesQuotePaymentStageMstr = doc.getElementsByTagName("tblSalesQuotePaymentStageMstr");
            for (int i = 0; i < tblSalesQuotePaymentStageMstr.getLength(); i++) {

                String PymtStageId = "0";
                String PymtStage = "0";
                String PymtModeId = "0";


                Element element = (Element) tblSalesQuotePaymentStageMstr.item(i);

                if (!element.getElementsByTagName("PymtStageId").equals(null)) {

                    NodeList PymtStageIdNode = element.getElementsByTagName("PymtStageId");
                    Element line = (Element) PymtStageIdNode.item(0);

                    if (PymtStageIdNode.getLength() > 0) {

                        PymtStageId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("PymtStage").equals(null)) {

                    NodeList PymtStageNode = element.getElementsByTagName("PymtStage");
                    Element line = (Element) PymtStageNode.item(0);

                    if (PymtStageNode.getLength() > 0) {

                        PymtStage = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("PymtModeId").equals(null)) {

                    NodeList PymtModeIdNode = element.getElementsByTagName("PymtModeId");
                    Element line = (Element) PymtModeIdNode.item(0);

                    if (PymtModeIdNode.getLength() > 0) {

                        PymtModeId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.SavetblSalesQuotePaymentStageMstr(PymtStageId, PymtStage, PymtModeId);
            }


            NodeList tblSalesQuoteTypeMstr = doc.getElementsByTagName("tblSalesQuoteTypeMstr");
            for (int i = 0; i < tblSalesQuoteTypeMstr.getLength(); i++) {

                String SalesQuotetypeId = "0";
                String SalesQuoteType = "0";


                Element element = (Element) tblSalesQuoteTypeMstr.item(i);

                if (!element.getElementsByTagName("SalesQuotetypeId").equals(null)) {

                    NodeList SalesQuotetypeIdNode = element.getElementsByTagName("SalesQuotetypeId");
                    Element line = (Element) SalesQuotetypeIdNode.item(0);

                    if (SalesQuotetypeIdNode.getLength() > 0) {

                        SalesQuotetypeId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SalesQuoteType").equals(null)) {

                    NodeList SalesQuoteTypeNode = element.getElementsByTagName("SalesQuoteType");
                    Element line = (Element) SalesQuoteTypeNode.item(0);

                    if (SalesQuoteTypeNode.getLength() > 0) {

                        SalesQuoteType = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.SavetblSalesQuoteTypeMstr(SalesQuotetypeId, SalesQuoteType);
            }


            NodeList tblSalesQuotePaymentStageModeMapMstr = doc.getElementsByTagName("tblSalesQuotePaymentStageModeMapMstr");
            for (int i = 0; i < tblSalesQuotePaymentStageModeMapMstr.getLength(); i++) {

                String PymtStageId = "0";
                String PymtModeId = "0";


                Element element = (Element) tblSalesQuotePaymentStageModeMapMstr.item(i);

                if (!element.getElementsByTagName("PymtStageId").equals(null)) {

                    NodeList PymtStageIdNode = element.getElementsByTagName("PymtStageId");
                    Element line = (Element) PymtStageIdNode.item(0);

                    if (PymtStageIdNode.getLength() > 0) {

                        PymtStageId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("PymtModeId").equals(null)) {

                    NodeList PymtModeIdNode = element.getElementsByTagName("PymtModeId");
                    Element line = (Element) PymtModeIdNode.item(0);

                    if (PymtModeIdNode.getLength() > 0) {

                        PymtModeId = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.SavetblSalesQuotePaymentStageModeMapMstr(PymtStageId, PymtModeId);
            }


            NodeList tblSalesQuoteSponsorMstr = doc.getElementsByTagName("tblSalesQuoteSponsorMstr");
            for (int i = 0; i < tblSalesQuoteSponsorMstr.getLength(); i++) {

                String SalesQuoteSponsorID = "0";
                String SponsorDescr = "0";
                String Ordr = "0";


                Element element = (Element) tblSalesQuoteSponsorMstr.item(i);

                if (!element.getElementsByTagName("SalesQuoteSponsorID").equals(null)) {

                    NodeList SalesQuoteSponsorIDNode = element.getElementsByTagName("SalesQuoteSponsorID");
                    Element line = (Element) SalesQuoteSponsorIDNode.item(0);

                    if (SalesQuoteSponsorIDNode.getLength() > 0) {

                        SalesQuoteSponsorID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SponsorDescr").equals(null)) {

                    NodeList SponsorDescrNode = element.getElementsByTagName("SponsorDescr");
                    Element line = (Element) SponsorDescrNode.item(0);

                    if (SponsorDescrNode.getLength() > 0) {

                        SponsorDescr = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("Ordr").equals(null)) {

                    NodeList OrdrNode = element.getElementsByTagName("Ordr");
                    Element line = (Element) OrdrNode.item(0);

                    if (OrdrNode.getLength() > 0) {

                        Ordr = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.saveTblSalesQuoteSponsorMstr(SalesQuoteSponsorID, SponsorDescr, Ordr);
            }

            NodeList tblManufacturerMstrMain = doc.getElementsByTagName("tblManufacturerMstrMain");
            for (int i = 0; i < tblManufacturerMstrMain.getLength(); i++) {

                String ManufacturerID = "0";
                String ManufacturerName = "0";
                String NodeType = "0";


                Element element = (Element) tblManufacturerMstrMain.item(i);

                if (!element.getElementsByTagName("ManufacturerID").equals(null)) {

                    NodeList ManufacturerIDNode = element.getElementsByTagName("ManufacturerID");
                    Element line = (Element) ManufacturerIDNode.item(0);

                    if (ManufacturerIDNode.getLength() > 0) {

                        ManufacturerID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ManufacturerName").equals(null)) {

                    NodeList ManufacturerNameNode = element.getElementsByTagName("ManufacturerName");
                    Element line = (Element) ManufacturerNameNode.item(0);

                    if (ManufacturerNameNode.getLength() > 0) {

                        ManufacturerName = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("NodeType").equals(null)) {

                    NodeList NodeTypeNode = element.getElementsByTagName("NodeType");
                    Element line = (Element) NodeTypeNode.item(0);

                    if (NodeTypeNode.getLength() > 0) {

                        NodeType = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.saveTblManufacturerMstrMain(ManufacturerID, ManufacturerName, NodeType);
            }

            NodeList tblRateDistributionNode = doc.getElementsByTagName("tblRateDistribution");
            for (int i = 0; i < tblRateDistributionNode.getLength(); i++) {

                String SalesQuoteId = "0";
                String StoreId = "0";
                String SalesQuoteSponsorID = "0";
                String ManufacturerID = "0";
                String Percentage = "0";
                String SponsorDescr = "0";
                String ManufacturerName = "0";

                Element element = (Element) tblRateDistributionNode.item(i);

                if (!element.getElementsByTagName("SalesQuoteId").equals(null)) {

                    NodeList SalesQuoteIdNode = element.getElementsByTagName("SalesQuoteId");
                    Element line = (Element) SalesQuoteIdNode.item(0);

                    if (SalesQuoteIdNode.getLength() > 0) {

                        SalesQuoteId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("StoreId").equals(null)) {

                    NodeList StoreIdNode = element.getElementsByTagName("StoreId");
                    Element line = (Element) StoreIdNode.item(0);

                    if (StoreIdNode.getLength() > 0) {

                        StoreId = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("SalesQuoteSponsorID").equals(null)) {

                    NodeList SalesQuoteSponsorIDNode = element.getElementsByTagName("SalesQuoteSponsorID");
                    Element line = (Element) SalesQuoteSponsorIDNode.item(0);

                    if (SalesQuoteSponsorIDNode.getLength() > 0) {

                        SalesQuoteSponsorID = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("ManufacturerID").equals(null)) {

                    NodeList ManufacturerIDNode = element.getElementsByTagName("ManufacturerID");
                    Element line = (Element) ManufacturerIDNode.item(0);

                    if (ManufacturerIDNode.getLength() > 0) {

                        ManufacturerID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("Percentage").equals(null)) {

                    NodeList PercentageNode = element.getElementsByTagName("Percentage");
                    Element line = (Element) PercentageNode.item(0);

                    if (PercentageNode.getLength() > 0) {

                        Percentage = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("SponsorDescr").equals(null)) {

                    NodeList SponsorDescrNode = element.getElementsByTagName("SponsorDescr");
                    Element line = (Element) SponsorDescrNode.item(0);

                    if (SponsorDescrNode.getLength() > 0) {

                        SponsorDescr = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("ManufacturerName").equals(null)) {

                    NodeList ManufacturerNameNode = element.getElementsByTagName("ManufacturerName");
                    Element line = (Element) ManufacturerNameNode.item(0);

                    if (ManufacturerNameNode.getLength() > 0) {

                        ManufacturerName = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.saveTblRateDistribution(SalesQuoteId, StoreId, SalesQuoteSponsorID, ManufacturerID, Percentage, SponsorDescr, ManufacturerName, "1");
            }


            setmovie.director = "1";
            // System.out.println("ServiceWorkerNitish getallStores Completed ");
            flagExecutedServiceSuccesfully = 37;
            return setmovie;


        } catch (Exception e) {

            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }


    //ByVal bydate As String, ByVal uuid As String, ByVal rID As Integer
    public ServiceWorker getAllPOSMaterialStructure(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnCallPOSMaterial";
        final String METHOD_NAME = "fnCallPOSMaterial";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        //Create request
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            //ByVal bydate As String, ByVal uuid As String, ByVal rID As Integer

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("uuid", uuid.toString());
            client.addProperty("rID", rID.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            String resultString = androidHttpTransport.responseDump;
            // remove information XML,only retrieved results that returned

            //	responseBody = (SoapObject) responseBody.getProperty(1);

            //int count123=responseBody.getPropertyCount();
            // get information XMl of tables that is returned
            String name = responseBody.getProperty(0).toString();
            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            dbengine.Delete_tblMaterialAndStoreIDMap();


            NodeList tblPOSMaterialMstrNode = doc.getElementsByTagName("tblPOSMaterialMstr");
            for (int i = 0; i < tblPOSMaterialMstrNode.getLength(); i++) {
                String POSMaterialID = "NA";
                String POSMaterialDescr = "NA";

                Element element = (Element) tblPOSMaterialMstrNode.item(i);
                NodeList POSMaterialIDNode = element.getElementsByTagName("POSMaterialID");
                Element line = (Element) POSMaterialIDNode.item(0);

                if (!element.getElementsByTagName("POSMaterialID").equals(null)) {
                    if (POSMaterialIDNode.getLength() > 0) {
                        POSMaterialID = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("POSMaterialDescr").equals(null)) {
                    NodeList POSMaterialDescrNode = element.getElementsByTagName("POSMaterialDescr");
                    line = (Element) POSMaterialDescrNode.item(0);
                    if (POSMaterialDescrNode.getLength() > 0) {
                        POSMaterialDescr = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblPOSMaterialMstr(POSMaterialID, POSMaterialDescr);

            }


            NodeList tblStoreIDAndMaterialIDMapNode = doc.getElementsByTagName("tblStoreIDAndMaterialIDMap");
            for (int i = 0; i < tblStoreIDAndMaterialIDMapNode.getLength(); i++) {


                String StoreID = "NA";
                String VisitID = "NA";
                String MaterialID = "NA";
                String CurrentStockQty = "NA";

                Element element = (Element) tblStoreIDAndMaterialIDMapNode.item(i);

                NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                Element line = (Element) StoreIDNode.item(0);

                if (!element.getElementsByTagName("StoreID").equals(null)) {
                    if (StoreIDNode.getLength() > 0) {
                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("VisitID").equals(null)) {
                    NodeList VisitIDNode = element.getElementsByTagName("VisitID");
                    line = (Element) VisitIDNode.item(0);
                    if (VisitIDNode.getLength() > 0) {
                        VisitID = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("MaterialID").equals(null)) {
                    NodeList MaterialIDNode = element.getElementsByTagName("MaterialID");
                    line = (Element) MaterialIDNode.item(0);
                    if (MaterialIDNode.getLength() > 0) {
                        MaterialID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("CurrentStockQty").equals(null)) {
                    NodeList CurrentStockQtyNode = element.getElementsByTagName("CurrentStockQty");
                    line = (Element) CurrentStockQtyNode.item(0);
                    if (CurrentStockQtyNode.getLength() > 0) {
                        CurrentStockQty = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.savetblStoreIDAndMaterialIDMap(StoreID, VisitID, MaterialID, CurrentStockQty);

            }


            setmovie.director = "1";
            dbengine.close();

            flagExecutedServiceSuccesfully = 4;
            return setmovie;

        } catch (Exception e) {


            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();

            return setmovie;
        }
    }

    public ServiceWorker getAllNewSchemeStructure(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnTestSchemeResultTest";
        final String METHOD_NAME = "fnTestSchemeResultTest";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        //Create request
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            //client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());
            //client.addProperty("rID", rID.toString());

			/*client.addProperty("bydate", dateVAL.toString());
			client.addProperty("IMEINo", uuid.toString());*/


            //// System.out.println("SRVC WRKR - dateVAL.toString(): "+dateVAL.toString());
            //	// System.out.println("SRVC WRKR - uuid.toString(): "+uuid.toString());

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            String resultString = androidHttpTransport.responseDump;
            // remove information XML,only retrieved results that returned

            //	responseBody = (SoapObject) responseBody.getProperty(1);

            //int count123=responseBody.getPropertyCount();
            // get information XMl of tables that is returned
            String name = responseBody.getProperty(0).toString();
            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            dbengine.Delete_tblStoreProductMap_for_refreshData();


            NodeList tblSchemeStoreMappingNode = doc.getElementsByTagName("SchemeStoreMapping");
            for (int i = 0; i < tblSchemeStoreMappingNode.getLength(); i++) {
                String StoreID = "NA";
                String SchemeID = "NA";
                Element element = (Element) tblSchemeStoreMappingNode.item(i);

                NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                Element line = (Element) StoreIDNode.item(0);
                StoreID = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeStoreMapping StoreID: " +StoreID );

                NodeList SchemeIDNode = element.getElementsByTagName("SchemeID");
                line = (Element) SchemeIDNode.item(0);
                SchemeID = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeStoreMapping SchemeID: " + SchemeID);
                dbengine.savetblSchemeStoreMapping(StoreID, SchemeID);

            }


            NodeList tblSchemeMstrNode = doc.getElementsByTagName("SchemeMstr");
            for (int i = 0; i < tblSchemeMstrNode.getLength(); i++) {
                String SchemeID = "NA";
                String SchemeName = "NA";
                String SchemeApplicationID = "NA";
                String SchemeAppliedRule = "NA";

                Element element = (Element) tblSchemeMstrNode.item(i);
                NodeList SchemeIDNode = element.getElementsByTagName("SchemeID");
                Element line = (Element) SchemeIDNode.item(0);
                SchemeID = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeMstr: SchemeID" +SchemeID );


                NodeList SchemeNameNode = element.getElementsByTagName("SchemeName");
                line = (Element) SchemeNameNode.item(0);
                SchemeName = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeMstr: SchemeName " +SchemeName );


                NodeList SchemeApplicationIDNode = element.getElementsByTagName("SchemeApplicationID");
                line = (Element) SchemeApplicationIDNode.item(0);
                SchemeApplicationID = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeMstr: SchemeApplicationID " +SchemeApplicationID );

                NodeList SchemeAppliedRuleNode = element.getElementsByTagName("SchemeAppliedRule");
                line = (Element) SchemeAppliedRuleNode.item(0);
                SchemeAppliedRule = xmlParser.getCharacterDataFromElement(line);
                // // System.out.println("Ajay tblSchemeMstr: SchemeAppliedRule " +SchemeAppliedRule );

                dbengine.savetblSchemeMstr(SchemeID, SchemeName, SchemeApplicationID, SchemeAppliedRule);

            }


            NodeList tblSchemeSlabDetailNode = doc.getElementsByTagName("SchemeSlabDetail");
            for (int i = 0; i < tblSchemeSlabDetailNode.getLength(); i++) {
                String SchemeID = "NA";
                String SchemeSlabID = "NA";
                String SchemeSlabDesc = "NA";
                String BenifitDescr = "NA";

                Element element = (Element) tblSchemeSlabDetailNode.item(i);
                NodeList SchemeIDNode = element.getElementsByTagName("SchemeID");
                Element line = (Element) SchemeIDNode.item(0);
                SchemeID = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeSlabDetail: SchemeID" +SchemeID );


                NodeList SchemeSlabIDNode = element.getElementsByTagName("SchemeSlabID");
                line = (Element) SchemeSlabIDNode.item(0);
                SchemeSlabID = xmlParser.getCharacterDataFromElement(line);
                // // System.out.println("Ajay tblSchemeSlabDetail: SchemeSlabID " +SchemeSlabID );


                NodeList SchemeSlabDescNode = element.getElementsByTagName("SchemeSlabDesc");
                line = (Element) SchemeSlabDescNode.item(0);
                SchemeSlabDesc = xmlParser.getCharacterDataFromElement(line);
                // System.out.println("Ajay tblSchemeSlabDetail: SchemeSlabDesc " +SchemeSlabDesc );

                NodeList BenifitDescrNode = element.getElementsByTagName("BenifitDescr");
                line = (Element) BenifitDescrNode.item(0);
                BenifitDescr = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeSlabDetail: BenifitDescr " +BenifitDescr );

                dbengine.savetblSchemeSlabDetail(SchemeID, SchemeSlabID, SchemeSlabDesc, BenifitDescr);

            }

	            /*tblSchemeSlabBucketDetails (RowID text null,SchemeID text null" +
				",SchemeSlabID text null,BucketID text null,SubBucketID text null,SlabSubBucketType text null" +
				",SlabSubBucketValue text null,SubBucketValType text null);";*/

            NodeList tblSchemeSlabBucketDetailsNode = doc.getElementsByTagName("SchemeSlabBucketDetails");
            for (int i = 0; i < tblSchemeSlabBucketDetailsNode.getLength(); i++) {
                String RowID = "NA";
                String SchemeID = "NA";
                String SchemeSlabID = "NA";
                String BucketID = "NA";
                String SubBucketID = "NA";
                String SlabSubBucketType = "NA";
                String SlabSubBucketValue = "NA";
                String SubBucketValType = "NA";


                Element element = (Element) tblSchemeSlabBucketDetailsNode.item(i);

                NodeList RowIDNode = element.getElementsByTagName("RowID");
                Element line = (Element) RowIDNode.item(0);
                RowID = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeSlabBucketDetails: RowID" +RowID );


                NodeList SchemeIDNode = element.getElementsByTagName("SchemeID");
                line = (Element) SchemeIDNode.item(0);
                SchemeID = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeSlabBucketDetails: SchemeID" +SchemeID );


                NodeList SchemeSlabIDNode = element.getElementsByTagName("SchemeSlabID");
                line = (Element) SchemeSlabIDNode.item(0);
                SchemeSlabID = xmlParser.getCharacterDataFromElement(line);

                NodeList BucketIDNode = element.getElementsByTagName("BucketID");
                line = (Element) BucketIDNode.item(0);
                BucketID = xmlParser.getCharacterDataFromElement(line);


                NodeList SubBucketIDNode = element.getElementsByTagName("SubBucketID");
                line = (Element) SubBucketIDNode.item(0);
                SubBucketID = xmlParser.getCharacterDataFromElement(line);

                NodeList SlabSubBucketTypeNode = element.getElementsByTagName("SlabSubBucketType");
                line = (Element) SlabSubBucketTypeNode.item(0);
                SlabSubBucketType = xmlParser.getCharacterDataFromElement(line);

                NodeList SlabSubBucketValueNode = element.getElementsByTagName("SlabSubBucketValue");
                line = (Element) SlabSubBucketValueNode.item(0);
                SlabSubBucketValue = xmlParser.getCharacterDataFromElement(line);

                NodeList SubBucketValTypeNode = element.getElementsByTagName("SubBucketValType");
                line = (Element) SubBucketValTypeNode.item(0);
                SubBucketValType = xmlParser.getCharacterDataFromElement(line);


                dbengine.savetblSchemeSlabBucketDetails(RowID, SchemeID, SchemeSlabID, BucketID, SubBucketID, SlabSubBucketType, SlabSubBucketValue, SubBucketValType);


            }


            // tblSchemeSlabBucketProductMapping (RowID text null,ProductID text null);";

            NodeList tblSchemeSlabBucketProductMappingNode = doc.getElementsByTagName("SchemeSlabBucketProductMapping");
            for (int i = 0; i < tblSchemeSlabBucketProductMappingNode.getLength(); i++) {
                String RowID = "NA";
                String ProductID = "NA";


                Element element = (Element) tblSchemeSlabBucketProductMappingNode.item(i);

                NodeList RowIDNode = element.getElementsByTagName("RowID");
                Element line = (Element) RowIDNode.item(0);
                RowID = xmlParser.getCharacterDataFromElement(line);
                // // System.out.println("Ajay tblSchemeSlabBucketProductMapping: RowID" +RowID );


                NodeList ProductIDNode = element.getElementsByTagName("ProductID");
                line = (Element) ProductIDNode.item(0);
                ProductID = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeSlabBucketProductMapping: ProductID" +ProductID );


                dbengine.savetblSchemeSlabBucketProductMapping(RowID, ProductID);

            }


	        /*    tblSchemeSlabBenefitsBucketDetails (RowID text null," +
	        			"SchemeID text null,SchemeSlabID text null,BucketID text null,SubBucketID text null," +
	        			"BenSubBucketType text null," +
	        			"BenDiscApplied text null,CouponCode text null,BenSubBucketValue text null);";*/

            NodeList tblSchemeSlabBenefitsBucketDetailsNode = doc.getElementsByTagName("SchemeSlabBenefitsBucketDetails");
            for (int i = 0; i < tblSchemeSlabBenefitsBucketDetailsNode.getLength(); i++) {
                String RowID = "NA";
                String SchemeID = "NA";
                String SchemeSlabID = "NA";
                String BucketID = "NA";
                String SubBucketID = "NA";
                String BenSubBucketType = "NA";
                String BenDiscApplied = "NA";
                String CouponCode = "NA";
                String BenSubBucketValue = "NA";
                String per = "0.00";
                String UOM = "0.00";
                int ProRata = 0;

                Element element = (Element) tblSchemeSlabBenefitsBucketDetailsNode.item(i);

                NodeList RowIDNode = element.getElementsByTagName("RowID");
                Element line = (Element) RowIDNode.item(0);
                RowID = xmlParser.getCharacterDataFromElement(line);
                // // System.out.println("Ajay tblSchemeSlabBucketDetails: RowID" +RowID );


                NodeList SchemeIDNode = element.getElementsByTagName("SchemeID");
                line = (Element) SchemeIDNode.item(0);
                SchemeID = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeSlabBucketDetails: SchemeID" +SchemeID );


                NodeList SchemeSlabIDNode = element.getElementsByTagName("SchemeSlabID");
                line = (Element) SchemeSlabIDNode.item(0);
                SchemeSlabID = xmlParser.getCharacterDataFromElement(line);

                NodeList BucketIDNode = element.getElementsByTagName("BucketID");
                line = (Element) BucketIDNode.item(0);
                BucketID = xmlParser.getCharacterDataFromElement(line);


                NodeList SubBucketIDNode = element.getElementsByTagName("SubBucketID");
                line = (Element) SubBucketIDNode.item(0);
                SubBucketID = xmlParser.getCharacterDataFromElement(line);


                NodeList BenSubBucketTypeNode = element.getElementsByTagName("BenSubBucketType");
                line = (Element) BenSubBucketTypeNode.item(0);
                BenSubBucketType = xmlParser.getCharacterDataFromElement(line);

                NodeList BenDiscAppliedNode = element.getElementsByTagName("BenDiscApplied");
                line = (Element) BenDiscAppliedNode.item(0);
                BenDiscApplied = xmlParser.getCharacterDataFromElement(line);

                NodeList CouponCodeNode = element.getElementsByTagName("CouponCode");
                line = (Element) CouponCodeNode.item(0);
                CouponCode = xmlParser.getCharacterDataFromElement(line);

                NodeList BenSubBucketValueNode = element.getElementsByTagName("BenSubBucketValue");
                line = (Element) BenSubBucketValueNode.item(0);
                BenSubBucketValue = xmlParser.getCharacterDataFromElement(line);

                NodeList perNode = element.getElementsByTagName("Per");
                line = (Element) perNode.item(0);
                per = xmlParser.getCharacterDataFromElement(line);
                if (per.equals("1000")) {
                    //// System.out.println("AnkitAlok"+per);

                }

                NodeList UOMNode = element.getElementsByTagName("UOM");
                line = (Element) UOMNode.item(0);
                UOM = xmlParser.getCharacterDataFromElement(line);


                NodeList perProRata = element.getElementsByTagName("ProRata");
                line = (Element) perProRata.item(0);
                ProRata = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));


                dbengine.savetblSchemeSlabBenefitsBucketDetails(RowID, SchemeID, SchemeSlabID, BucketID,
                        SubBucketID, BenSubBucketType, BenDiscApplied, CouponCode, BenSubBucketValue, per, UOM, ProRata);


            }


            // tblSchemeSlabBenefitsProductMappingDetail (RowID text null,ProductID text null);";

            NodeList tblSchemeSlabBenefitsProductMappingDetailNode = doc.getElementsByTagName("SchemeSlabBenefitsProductMappingDetail");
            for (int i = 0; i < tblSchemeSlabBenefitsProductMappingDetailNode.getLength(); i++) {
                String RowID = "NA";
                String ProductID = "NA";


                Element element = (Element) tblSchemeSlabBenefitsProductMappingDetailNode.item(i);

                NodeList RowIDNode = element.getElementsByTagName("RowID");
                Element line = (Element) RowIDNode.item(0);
                RowID = xmlParser.getCharacterDataFromElement(line);
                // // System.out.println("Ajay tblSchemeSlabBucketProductMapping: RowID" +RowID );


                NodeList ProductIDNode = element.getElementsByTagName("ProductID");
                line = (Element) ProductIDNode.item(0);
                ProductID = xmlParser.getCharacterDataFromElement(line);
                //   // System.out.println("Ajay tblSchemeSlabBucketProductMapping: ProductID" +ProductID );


                dbengine.savetblSchemeSlabBenefitsProductMappingDetail(RowID, ProductID);

            }


            //  tblSchemeSlabBenefitsValueDetail (RowID text null,BenValue text null,Remarks text null,Type text null);";


            NodeList tblSchemeSlabBenefitsValueDetailNode = doc.getElementsByTagName("SchemeSlabBenefitsValueDetail");
            for (int i = 0; i < tblSchemeSlabBenefitsValueDetailNode.getLength(); i++) {
                String RowID = "NA";
                String BenValue = "NA";
                String Remarks = "NA";
                String Type = "NA";

                Element element = (Element) tblSchemeSlabBenefitsValueDetailNode.item(i);
                NodeList RowIDNode = element.getElementsByTagName("RowID");
                Element line = (Element) RowIDNode.item(0);
                RowID = xmlParser.getCharacterDataFromElement(line);
                //  // System.out.println("Ajay tblSchemeSlabBenefitsValueDetail: SchemeID" +RowID );


                NodeList BenValueNode = element.getElementsByTagName("BenValue");
                line = (Element) BenValueNode.item(0);
                BenValue = xmlParser.getCharacterDataFromElement(line);


                NodeList RemarksNode = element.getElementsByTagName("Remarks");
                line = (Element) RemarksNode.item(0);
                Remarks = xmlParser.getCharacterDataFromElement(line);

                NodeList TypeNode = element.getElementsByTagName("Type");
                line = (Element) TypeNode.item(0);
                Type = xmlParser.getCharacterDataFromElement(line);

                dbengine.savetblSchemeSlabBenefitsValueDetail(RowID, BenValue, Remarks, Type);

            }


            //  tblProductRelatedScheme (ProductID text null,PrdString text null);";

            NodeList tblProductRelatedSchemeNode = doc.getElementsByTagName("ProductRelatedScheme");
            for (int i = 0; i < tblProductRelatedSchemeNode.getLength(); i++) {
                String ProductID = "NA";
                String PrdString = "NA";


                Element element = (Element) tblProductRelatedSchemeNode.item(i);

                NodeList ProductIDNode = element.getElementsByTagName("ProductID");
                Element line = (Element) ProductIDNode.item(0);
                ProductID = xmlParser.getCharacterDataFromElement(line);


                NodeList PrdStringNode = element.getElementsByTagName("PrdString");
                line = (Element) PrdStringNode.item(0);
                PrdString = xmlParser.getCharacterDataFromElement(line);


                dbengine.savetblProductRelatedScheme(ProductID, PrdString);

            }


            setmovie.director = "1";
            dbengine.close();
            // System.out.println("ServiceWorkerNitish getAllNewSchemeStructure Inside");
            flagExecutedServiceSuccesfully = 4;
            return setmovie;

        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetIMEIVersionDetailStatus :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();

            return setmovie;
        }
    }


    public ServiceWorker getAvailableAndUpdatedVersionOfApp(Context ctx, String uuid, String CurDate, int DatabaseVersion, int ApplicationID) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        decimalFormat.applyPattern(pattern);
        final String SOAP_ACTION = "http://tempuri.org/GetIMEIVersionDetailStatus";
        final String METHOD_NAME = "GetIMEIVersionDetailStatus";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);
            // System.out.println("anil testing Service worker DatabaseVersion :"+DatabaseVersion);
            client.addProperty("uuid", uuid.toString());
            client.addProperty("DatabaseVersion", DatabaseVersion);
            client.addProperty("ApplicationID", ApplicationID);
            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //////// System.out.println("chkAvailbUpdatedVersion "+ chkTblStoreListContainsRow);

            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 0) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        tableRow = (SoapObject) table.getProperty(i);


                        String VersionID = "0";
                        String VersionSerialNo = "NA";
                        String VersionDownloadStatus = "NA";
                        Date pdaDate = new Date();
                        SimpleDateFormat sdfPDaDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                        String fDatePda = sdfPDaDate.format(pdaDate).toString().trim();
                        String ServerDate = fDatePda;


                        if ((!tableRow.hasProperty("VersionID"))) {
                        } else {
                            VersionID = tableRow.getProperty("VersionID").toString().trim();
                        }

                        if ((!tableRow.hasProperty("VersionSerialNo"))) {
                        } else {
                            VersionSerialNo = tableRow.getProperty("VersionSerialNo").toString().trim();

                        }

                        if ((!tableRow.hasProperty("VersionDownloadStatus"))) {
                        } else {
                            VersionDownloadStatus = tableRow.getProperty("VersionDownloadStatus").toString().trim();
                        }

                        if ((!tableRow.hasProperty("ServerDate"))) {
                        } else {
                            ServerDate = tableRow.getProperty("ServerDate").toString().trim();
                        }

                        dbengine.savetblAvailbUpdatedVersion(VersionID.trim(), VersionSerialNo.trim(), VersionDownloadStatus.trim(), ServerDate);


                    }
                }
            }


            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;

        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetIMEIVersionDetailStatus :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }

    }

    public ServiceWorker getAvailbRoutes(Context ctx, String uuid, String dateVAL, String RegistrationID, int flgTodaySalesTargetToShow) {

        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);

        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/GetRoutes";
        final String METHOD_NAME = "GetRoutes";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        //Create request
        SoapObject table = null; //Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; //Its the client petition to the web service
        SoapObject tableRow = null; //Contains row of table
        SoapObject responseBody = null; //Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;
        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 0);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);
				/*client.addProperty("uuid", uuid.toString());
				client.addProperty("DatabaseVersion", DatabaseVersionID);
				client.addProperty("ApplicationID", ApplicationID);//ContactNo
				client.addProperty("ContactNo", ContactNo);*/


				/*try
				{
					Thread.sleep(4000);
				}
				catch(Exception e)
				{

				}*/


            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("AppVersionID", dbengine.AppVersionID.toString());
            client.addProperty("RegistrationID", RegistrationID);
            client.addProperty("flgTodaySalesTargetToShow", 1);

            //flgTodaySalesTargetToShow

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);


            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            dbengine.open();

            dbengine.Delete_tblRouteMasterAndDistributorMstr();

            NodeList tblRouteListMasterNode = doc.getElementsByTagName("tblRouteListMaster");
            for (int i = 0; i < tblRouteListMasterNode.getLength(); i++) {
                String stID = "NA";
                String deDescr = "NA";
                int Active = 0;
                int flgTodayRoute = 0;
                String RouteDate = "06-Aug-2015";

                String RouteType = "0";

                Element element = (Element) tblRouteListMasterNode.item(i);


                // Note by Sunil---> This RouteType field is not coming in Meiji DB now (03-feb-2017) but coming in other DB so we are handle this field functinoality

                Element line;

                if (!element.getElementsByTagName("ID").equals(null)) {
                    NodeList IDNode = element.getElementsByTagName("ID");
                    line = (Element) IDNode.item(0);
                    if (IDNode.getLength() > 0) {
                        stID = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("RouteType").equals(null)) {
                    NodeList RouteTypeNode = element.getElementsByTagName("RouteType");
                    line = (Element) RouteTypeNode.item(0);
                    if (RouteTypeNode.getLength() > 0) {
                        RouteType = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Descr").equals(null)) {
                    NodeList DescrNode = element.getElementsByTagName("Descr");
                    line = (Element) DescrNode.item(0);
                    if (DescrNode.getLength() > 0) {
                        deDescr = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("Active").equals(null)) {
                    NodeList ActiveNode = element.getElementsByTagName("Active");
                    line = (Element) ActiveNode.item(0);
                    if (ActiveNode.getLength() > 0) {
                        Active = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("Date").equals(null)) {
                    NodeList DateNode = element.getElementsByTagName("Date");
                    line = (Element) DateNode.item(0);
                    if (DateNode.getLength() > 0) {
                        RouteDate = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (Active == 1) {
                    flgTodayRoute = 1;

                } else {
                    flgTodayRoute = 0;
                }

                dbengine.saveRoutesInfo(stID.trim(), RouteType.trim(), deDescr, Active, flgTodayRoute, RouteDate);


            }

            NodeList tblDistributorListMasterNode = doc.getElementsByTagName("tblDistributorListMaster");
            for (int i = 0; i < tblDistributorListMasterNode.getLength(); i++) {


                int DBRNodeID = 0;
                int DistributorNodeType = 0;
                String Distributor = "NA";

                Element element = (Element) tblDistributorListMasterNode.item(i);

                Element line;

                if (!element.getElementsByTagName("DBRNodeID").equals(null)) {
                    NodeList DBRNodeIDNode = element.getElementsByTagName("DBRNodeID");
                    line = (Element) DBRNodeIDNode.item(0);
                    if (DBRNodeIDNode.getLength() > 0) {
                        DBRNodeID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }


                if (!element.getElementsByTagName("DistributorNodeType").equals(null)) {
                    NodeList DistributorNodeTypeNode = element.getElementsByTagName("DistributorNodeType");
                    line = (Element) DistributorNodeTypeNode.item(0);
                    if (DistributorNodeTypeNode.getLength() > 0) {
                        DistributorNodeType = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("Distributor").equals(null)) {
                    NodeList DistributorNode = element.getElementsByTagName("Distributor");
                    line = (Element) DistributorNode.item(0);
                    if (DistributorNode.getLength() > 0) {
                        Distributor = (xmlParser.getCharacterDataFromElement(line));
                    }
                }


                dbengine.savetblDistributorListMaster(DBRNodeID, DistributorNodeType, Distributor);
            }

            NodeList tblSalesPersonTodaysTarget = doc.getElementsByTagName("tblSalesPersonTodaysTarget");
            CommonInfo.SalesPersonTodaysTargetMsg = "";
            for (int i = 0; i < tblSalesPersonTodaysTarget.getLength(); i++) {
                Element element = (Element) tblSalesPersonTodaysTarget.item(i);
                Element line;
                if (!element.getElementsByTagName("ValueTarget").equals(null)) {
                    NodeList ValueTargetNode = element.getElementsByTagName("ValueTarget");
                    line = (Element) ValueTargetNode.item(0);
                    if (ValueTargetNode.getLength() > 0) {
                        CommonInfo.SalesPersonTodaysTargetMsg = xmlParser.getCharacterDataFromElement(line);
                    }
                }
            }
            dbengine.close();
            setmovie.director = "1";
            return setmovie;

        } catch (Exception e) {

            //System.out.println("Aman Exception occur in GetIMEIVersionDetailStatusNew :"+e.toString());
            if (e.toString().contains("SocketTimeoutException") || e.toString().contains("ConnectException") || e.toString().contains("SocketException")) {
                setmovie.director = "100";
            } else if (e.toString().contains("NullPointerException")) {
                setmovie.director = "200";
            } else {
                setmovie.director = e.toString();
            }

            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }


    }


    public ServiceWorker getAvailbNotification(Context ctx, String uuid, String dateVAL) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);

        final String SOAP_ACTION = "http://tempuri.org/fnGetPDANotificationList";
        final String METHOD_NAME = "fnGetPDANotificationList";//"GetRoutesWithOnOffRouteFlg";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {

            dbengine.open();

            client = new SoapObject(NAMESPACE, METHOD_NAME);

            // System.out.println("Sunil Calling getAvailbNotification :");

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("AppVersionID", dbengine.AppVersionID.toString());

			/*//// System.out.println("Yes Raja Babu SRVC WRKR - dateVAL.toString(): "+dateVAL.toString());
			//// System.out.println("Yes Raja Babu SRVC WRKR - uuid.toString(): "+uuid.toString());
			//// System.out.println("Yes Raja Babu SRVC WRKR - AppVersionID: "+dbengine.AppVersionID.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            // System.out.println("Sunil Calling getAvailbNotification 1:");
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();

            //////// System.out.println("Dangi one :"+responseBody);
            // remove information XML,only retrieved results that returned
            // System.out.println("Sunil Calling getAvailbNotification 2:");
            responseBody = (SoapObject) responseBody.getProperty(1);
            ////// System.out.println("Dangi two :"+responseBody);
            // System.out.println("Sunil Calling getAvailbNotification 2.1 :"+responseBody);
            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            ////// System.out.println("Dangi three  :"+table);
            // System.out.println("Sunil Calling getAvailbNotification 3: "+table);
            // #1

            //dbengine.reCreateDB();
            ////// System.out.println("Debug: " + "dbengine.open");
            chkTblStoreListContainsRow = 1;
            if (chkTblStoreListContainsRow == 1) {
                // System.out.println("Sunil Calling getAvailbNotification 4:");
                dbengine.Delete_tblNotificationMstr();
                if (table.getPropertyCount() > 0) {
                    int SerialNo = 0;

                    // System.out.println("Sunil Calling getAvailbNotification 5:");
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                        // System.out.println("Sunil Calling getAvailbNotification 6:");

                        ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        ////// System.out.println("i value: "+ i);

                        ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        ////// System.out.println("i value: "+ i);

				/*NotificationMessage
				MsgSendingTime*/

                        String NotificationMessage = "NA";
                        String MsgSendingTime = "NA";
                        int MsgServerID = 0;


                        if ((!tableRow.hasProperty("NotificationMessage"))) {
                        } else {
                            // System.out.println("Sunil Calling getAvailbNotification 7:");
                            NotificationMessage = tableRow.getProperty("NotificationMessage").toString().trim();
                        }

                        if ((!tableRow.hasProperty("MsgSendingTime"))) {
                        } else {
                            // System.out.println("Sunil Calling getAvailbNotification 8:");
                            MsgSendingTime = tableRow.getProperty("MsgSendingTime").toString().trim();
                        }
                        if ((!tableRow.hasProperty("MsgServerID"))) {
                        } else {
                            // System.out.println("Sunil Calling getAvailbNotification MsgServerID : "+MsgServerID);
                            MsgServerID = Integer.parseInt(tableRow.getProperty("MsgServerID").toString().trim());
                        }

                        // System.out.println("Sunil Calling getAvailbNotification 9: "+NotificationMessage);
                        // System.out.println("Sunil Calling getAvailbNotification 10: "+MsgSendingTime);

                        SerialNo = i + 1;

                        dbengine.inserttblNotificationMstr(SerialNo, uuid, NotificationMessage, MsgSendingTime, 0, 0,
                                "0", 0, MsgServerID);


                    }
                }
            }


            dbengine.close();

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {

            // System.out.println("aman getAvailbNotification "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }


    public ServiceWorker callfnSingleCallAllWebService(Context ctx, int ApplicationID, String uuid) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnSingleCallAllStoreMappingMeijiTT";
        final String METHOD_NAME = "fnSingleCallAllStoreMappingMeijiTT";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        //Create request
        SoapObject table = null; //Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; //Its the client petition to the web service
        SoapObject tableRow = null; //Contains row of table
        SoapObject responseBody = null; //Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;
        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 0);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);
            client.addProperty("ApplicationID", ApplicationID);
            client.addProperty("uuid", uuid.toString());

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            dbengine.deleteAllSingleCallWebServiceTable();
            dbengine.open();
            int gblQuestIDForOutChannel = 0;
            NodeList tblQuestIDForOutChannel = doc.getElementsByTagName("tblQuestIDForOutChannel");
            for (int i = 0; i < tblQuestIDForOutChannel.getLength(); i++) {
                int grpQuestId = 0;
                int questId = 0;
                String optId = "0-0-0";
                int sectionCount = 0;
                Element element = (Element) tblQuestIDForOutChannel.item(i);
                if (!element.getElementsByTagName("GrpQuestID").equals(null)) {
                    NodeList QuestIDForOutChannelNode = element.getElementsByTagName("GrpQuestID");
                    Element line = (Element) QuestIDForOutChannelNode.item(0);
                    if (QuestIDForOutChannelNode.getLength() > 0) {
                        grpQuestId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                if (!element.getElementsByTagName("QuestID").equals(null)) {
                    NodeList QuestIDNode = element.getElementsByTagName("QuestID");
                    Element line = (Element) QuestIDNode.item(0);
                    if (QuestIDNode.getLength() > 0) {
                        questId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                        gblQuestIDForOutChannel = questId;
                    }
                }
                if (!element.getElementsByTagName("OptionID").equals(null)) {
                    NodeList optIDNode = element.getElementsByTagName("OptionID");
                    Element line = (Element) optIDNode.item(0);
                    if (optIDNode.getLength() > 0) {
                        optId = xmlParser.getCharacterDataFromElement(line);

                    }
                }
                if (!element.getElementsByTagName("SectionCount").equals(null)) {
                    NodeList SectionCountNode = element.getElementsByTagName("SectionCount");
                    Element line = (Element) SectionCountNode.item(0);
                    if (SectionCountNode.getLength() > 0) {
                        sectionCount = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                    }
                }
                dbengine.saveOutletChammetQstnIdGrpId(grpQuestId, questId, optId, sectionCount);
            }

            NodeList tblQuestIDForName = doc.getElementsByTagName("tblQuestIDForName");
            for (int i = 0; i < tblQuestIDForName.getLength(); i++) {
                int grpQuestId = 0;
                int questId = 0;
                Element element = (Element) tblQuestIDForName.item(i);
                if (!element.getElementsByTagName("GrpQuestID").equals(null)) {
                    NodeList QuestIDForOutChannelNode = element.getElementsByTagName("GrpQuestID");
                    Element line = (Element) QuestIDForOutChannelNode.item(0);
                    if (QuestIDForOutChannelNode.getLength() > 0) {
                        grpQuestId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                if (!element.getElementsByTagName("QuestID").equals(null)) {
                    NodeList QuestIDNode = element.getElementsByTagName("QuestID");
                    Element line = (Element) QuestIDNode.item(0);
                    if (QuestIDNode.getLength() > 0) {
                        questId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                    }
                }
                dbengine.savetblQuestIDForName(grpQuestId, questId);
            }
            NodeList tblSPGetDistributorDetailsNode = doc.getElementsByTagName("tblGetPDAQuestMstr");
            for (int i = 0; i < tblSPGetDistributorDetailsNode.getLength(); i++) {
                String QuestID = "0";
                String QuestCode = "0";
                String QuestDesc = "0";
                String QuestType = "0";

                String AnsControlType = "0";
                String AsnControlInputTypeID = "0";
                String AnsControlInputTypeMaxLength = "0";
                String AnsControlInputTypeMinLength = "0";
                String AnsMustRequiredFlg = "0";
                String QuestBundleFlg = "0";
                String ApplicationTypeID = "0";
                String Sequence = "0";
                String answerHint = "N/A";

                int flgQuestIDForOutChannel = 0;

                Element element = (Element) tblSPGetDistributorDetailsNode.item(i);


                if (!element.getElementsByTagName("QuestID").equals(null)) {
                    NodeList QuestIDNode = element.getElementsByTagName("QuestID");
                    Element line = (Element) QuestIDNode.item(0);
                    if (QuestIDNode.getLength() > 0) {
                        QuestID = xmlParser.getCharacterDataFromElement(line);
                        if (gblQuestIDForOutChannel == Integer.parseInt(QuestID)) {
                            flgQuestIDForOutChannel = 1;
                        }
                    }
                }
                if (!element.getElementsByTagName("QuestCode").equals(null)) {
                    NodeList QuestCodeNode = element.getElementsByTagName("QuestCode");
                    Element line = (Element) QuestCodeNode.item(0);
                    if (QuestCodeNode.getLength() > 0) {
                        QuestCode = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("QuestDesc").equals(null)) {
                    NodeList QuestDescNode = element.getElementsByTagName("QuestDesc");
                    Element line = (Element) QuestDescNode.item(0);
                    if (QuestDescNode.getLength() > 0) {
                        QuestDesc = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("QuestType").equals(null)) {
                    NodeList QuesTypeNode = element.getElementsByTagName("QuestType");
                    Element line = (Element) QuesTypeNode.item(0);
                    if (QuesTypeNode.getLength() > 0) {
                        QuestType = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("AnsControlType").equals(null)) {
                    NodeList AnsControlTypeNode = element.getElementsByTagName("AnsControlType");
                    Element line = (Element) AnsControlTypeNode.item(0);
                    if (AnsControlTypeNode.getLength() > 0) {
                        AnsControlType = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("AsnControlInputTypeID").equals(null)) {
                    NodeList AsnControlInputTypeIDNode = element.getElementsByTagName("AsnControlInputTypeID");
                    Element line = (Element) AsnControlInputTypeIDNode.item(0);
                    if (AsnControlInputTypeIDNode.getLength() > 0) {
                        AsnControlInputTypeID = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("AnsControlInputTypeMaxLength").equals(null)) {
                    NodeList AnsControlInputTypeMaxLengthNode = element.getElementsByTagName("AnsControlInputTypeMaxLength");
                    Element line = (Element) AnsControlInputTypeMaxLengthNode.item(0);
                    if (AnsControlInputTypeMaxLengthNode.getLength() > 0) {
                        AnsControlInputTypeMaxLength = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("AnsControlIntputTypeMinLength").equals(null)) {
                    NodeList AnsControlInputTypeMinLengthNode = element.getElementsByTagName("AnsControlIntputTypeMinLength");
                    Element line = (Element) AnsControlInputTypeMinLengthNode.item(0);
                    if (AnsControlInputTypeMinLengthNode.getLength() > 0) {
                        AnsControlInputTypeMinLength = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("AnsMustRequiredFlg").equals(null)) {
                    NodeList AnsMustRequiredFlgNode = element.getElementsByTagName("AnsMustRequiredFlg");
                    Element line = (Element) AnsMustRequiredFlgNode.item(0);
                    if (AnsMustRequiredFlgNode.getLength() > 0) {
                        AnsMustRequiredFlg = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("QuestBundleFlg").equals(null)) {
                    NodeList QuestBundleFlgNode = element.getElementsByTagName("QuestBundleFlg");
                    Element line = (Element) QuestBundleFlgNode.item(0);
                    if (QuestBundleFlgNode.getLength() > 0) {
                        QuestBundleFlg = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("ApplicationTypeID").equals(null)) {
                    NodeList ApplicationTypeIDNode = element.getElementsByTagName("ApplicationTypeID");
                    Element line = (Element) ApplicationTypeIDNode.item(0);
                    if (ApplicationTypeIDNode.getLength() > 0) {
                        ApplicationTypeID = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("Sequence").equals(null)) {
                    NodeList SequenceNode = element.getElementsByTagName("Sequence");
                    Element line = (Element) SequenceNode.item(0);
                    if (SequenceNode.getLength() > 0) {
                        Sequence = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("AnswerHint").equals(null)) {
                    NodeList SequenceNode = element.getElementsByTagName("AnswerHint");
                    Element line = (Element) SequenceNode.item(0);
                    if (SequenceNode.getLength() > 0) {
                        answerHint = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblQuestionMstr(QuestID, QuestCode, QuestDesc, QuestType, AnsControlType, AsnControlInputTypeID, AnsControlInputTypeMaxLength, AnsMustRequiredFlg, QuestBundleFlg, ApplicationTypeID, Sequence, AnsControlInputTypeMinLength, answerHint, flgQuestIDForOutChannel);

            }

            // dbengine.savetblQuestionMstr(QuestID, QuestCode, QuestDesc, QuestType, AnsControlType, AsnControlInputTypeID, AnsControlInputTypeMaxLength, AnsMustRequiredFlg, QuestBundleFlg, ApplicationTypeID, Sequence,AnsControlInputTypeMinLength,answerHint,flgQuestIDForOutChannel);

            // }

            NodeList tblGetPDAQuestGrpMappingNode = doc.getElementsByTagName("tblGetPDAQuestGrpMapping");
            for (int i = 0; i < tblGetPDAQuestGrpMappingNode.getLength(); i++) {
                String GrpQuestID = "0";
                String QuestID = "0";
                String GrpID = "0";
                String GrpNodeID = "0";

                String GrpDesc = "0";
                String SectionNo = "0";
                String GrpCopyID = "0";
                String QuestCopyID = "0";
                String sequence = "0";

                Element element = (Element) tblGetPDAQuestGrpMappingNode.item(i);


                if (!element.getElementsByTagName("GrpQuestID").equals(null)) {
                    NodeList GrpQuestIDNode = element.getElementsByTagName("GrpQuestID");
                    Element line = (Element) GrpQuestIDNode.item(0);
                    if (GrpQuestIDNode.getLength() > 0) {
                        GrpQuestID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("QuestID").equals(null)) {
                    NodeList QuestIDNode = element.getElementsByTagName("QuestID");
                    Element line = (Element) QuestIDNode.item(0);
                    if (QuestIDNode.getLength() > 0) {
                        QuestID = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("GrpID").equals(null)) {
                    NodeList GrpIDNode = element.getElementsByTagName("GrpID");
                    Element line = (Element) GrpIDNode.item(0);
                    if (GrpIDNode.getLength() > 0) {
                        GrpID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("GrpNodeID").equals(null)) {
                    NodeList GrpNodeIDNode = element.getElementsByTagName("GrpNodeID");
                    Element line = (Element) GrpNodeIDNode.item(0);
                    if (GrpNodeIDNode.getLength() > 0) {
                        GrpNodeID = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("GrpDesc").equals(null)) {
                    NodeList GrpDescNode = element.getElementsByTagName("GrpDesc");
                    Element line = (Element) GrpDescNode.item(0);
                    if (GrpDescNode.getLength() > 0) {
                        GrpDesc = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("SectionNo").equals(null)) {
                    NodeList SectionNoNode = element.getElementsByTagName("SectionNo");
                    Element line = (Element) SectionNoNode.item(0);
                    if (SectionNoNode.getLength() > 0) {
                        SectionNo = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("GrpCopyID").equals(null)) {
                    NodeList GrpCopyIDNode = element.getElementsByTagName("GrpCopyID");
                    Element line = (Element) GrpCopyIDNode.item(0);
                    if (GrpCopyIDNode.getLength() > 0) {
                        GrpCopyID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("QuestCopyID").equals(null)) {
                    NodeList QuestCopyIDNode = element.getElementsByTagName("QuestCopyID");
                    Element line = (Element) QuestCopyIDNode.item(0);
                    if (QuestCopyIDNode.getLength() > 0) {
                        QuestCopyID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("Sequence").equals(null)) {
                    NodeList SequenceNode = element.getElementsByTagName("Sequence");
                    Element line = (Element) SequenceNode.item(0);
                    if (SequenceNode.getLength() > 0) {
                        sequence = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblPDAQuestGrpMappingMstr(GrpQuestID, QuestID, GrpID, GrpNodeID, GrpDesc, SectionNo, GrpCopyID, QuestCopyID, sequence);

            }

            // dbengine.savetblPDAQuestGrpMappingMstr(GrpQuestID, QuestID, GrpID, GrpNodeID, GrpDesc, SectionNo,GrpCopyID,QuestCopyID);

            // }

            NodeList tblGetPDAQuestOptionMstrNode = doc.getElementsByTagName("tblGetPDAQuestOptionMstr");
            for (int i = 0; i < tblGetPDAQuestOptionMstrNode.getLength(); i++) {

                String OptID = "0";
                String QuestID = "0";
                String OptionNo = "0";
                String OptionDescr = "0";
                String Sequence = "0";


                Element element = (Element) tblGetPDAQuestOptionMstrNode.item(i);

                if (!element.getElementsByTagName("OptID").equals(null)) {
                    NodeList OptIDNode = element.getElementsByTagName("OptID");
                    Element line = (Element) OptIDNode.item(0);
                    if (OptIDNode.getLength() > 0) {
                        OptID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("QuestID").equals(null)) {
                    NodeList QuestIDNode = element.getElementsByTagName("QuestID");
                    Element line = (Element) QuestIDNode.item(0);
                    if (QuestIDNode.getLength() > 0) {
                        QuestID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("AnsVal").equals(null)) {
                    NodeList OptionNoDNode = element.getElementsByTagName("AnsVal");
                    Element line = (Element) OptionNoDNode.item(0);
                    if (OptionNoDNode.getLength() > 0) {
                        OptionNo = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("OptionDescr").equals(null)) {
                    NodeList OptionDescrNode = element.getElementsByTagName("OptionDescr");
                    Element line = (Element) OptionDescrNode.item(0);
                    if (OptionDescrNode.getLength() > 0) {
                        OptionDescr = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Sequence").equals(null)) {
                    NodeList SequenceNode = element.getElementsByTagName("Sequence");
                    Element line = (Element) SequenceNode.item(0);
                    if (SequenceNode.getLength() > 0) {

                        Sequence = xmlParser.getCharacterDataFromElement(line);

                    }
                }
                dbengine.savetblOptionMstr(OptID, QuestID, OptionNo, OptionDescr, Sequence);
                System.out.println("OptID:" + OptID + "QuestID:" + QuestID + "OptionNo:" + OptionNo + "OptionDescr:" + OptionDescr + "Sequence:" + Sequence);

            }
            // dbengine.savetblOptionMstr(OptID, QuestID, OptionNo, OptionDescr, Sequence);

            // }


            NodeList tblGetPDAQuestionDependentMstrNode = doc.getElementsByTagName("tblGetPDAQuestionDependentMstr");
            for (int i = 0; i < tblGetPDAQuestionDependentMstrNode.getLength(); i++) {


                String QuestionID = "0";
                String OptionID = "0";
                String DependentQuestionID = "0";

                String GrpID = "0";
                String DpndntGrpID = "0";


                Element element = (Element) tblGetPDAQuestionDependentMstrNode.item(i);


                if (!element.getElementsByTagName("QuestID").equals(null)) {
                    NodeList QuestionIDNode = element.getElementsByTagName("QuestID");
                    Element line = (Element) QuestionIDNode.item(0);
                    if (QuestionIDNode.getLength() > 0) {
                        QuestionID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("OptID").equals(null)) {
                    NodeList OptionIDNode = element.getElementsByTagName("OptID");
                    Element line = (Element) OptionIDNode.item(0);
                    if (OptionIDNode.getLength() > 0) {
                        OptionID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("DependentQuestionID").equals(null)) {
                    NodeList DependentQuestionIDNode = element.getElementsByTagName("DependentQuestionID");
                    Element line = (Element) DependentQuestionIDNode.item(0);
                    if (DependentQuestionIDNode.getLength() > 0) {
                        DependentQuestionID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("GrpQuestID").equals(null)) {
                    NodeList GrpIDNode = element.getElementsByTagName("GrpQuestID");
                    Element line = (Element) GrpIDNode.item(0);
                    if (GrpIDNode.getLength() > 0) {
                        GrpID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("GrpDepQuestID").equals(null)) {
                    NodeList GrpDepQuestIDNode = element.getElementsByTagName("GrpDepQuestID");
                    Element line = (Element) GrpDepQuestIDNode.item(0);
                    if (GrpDepQuestIDNode.getLength() > 0) {
                        DpndntGrpID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                dbengine.savetblQuestionDependentMstr(QuestionID, OptionID, DependentQuestionID, GrpID, DpndntGrpID);

                //  dbengine.savetblOptionMstr(OptID, QuestID, OptionNo, OptionDescr, Sequence);
                System.out.println("QuestionID:" + QuestionID + "OptionID:" + OptionID + "DependentQuestionID:" + DependentQuestionID);

            }

            NodeList tblPDAQuestOptionDependentMstr = doc.getElementsByTagName("tblPDAQuestOptionDependentMstr");
            for (int i = 0; i < tblPDAQuestOptionDependentMstr.getLength(); i++) {
                int qstId = 0;
                int depQstId = 0;
                int grpQstId = 0;
                int grpDepQstId = 0;
                Element element = (Element) tblPDAQuestOptionDependentMstr.item(i);


                if (!element.getElementsByTagName("QstId").equals(null)) {
                    NodeList QstIdNode = element.getElementsByTagName("QstId");
                    Element line = (Element) QstIdNode.item(0);
                    if (QstIdNode.getLength() > 0) {
                        qstId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                if (!element.getElementsByTagName("DepQstId").equals(null)) {
                    NodeList DepQstIdNode = element.getElementsByTagName("DepQstId");
                    Element line = (Element) DepQstIdNode.item(0);
                    if (DepQstIdNode.getLength() > 0) {
                        depQstId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }


                if (!element.getElementsByTagName("GrpQuestID").equals(null)) {
                    NodeList GrpQuestIDNode = element.getElementsByTagName("GrpQuestID");
                    Element line = (Element) GrpQuestIDNode.item(0);
                    if (GrpQuestIDNode.getLength() > 0) {
                        grpQstId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("GrpDepQuestID").equals(null)) {
                    NodeList GrpDepQuestIDNode = element.getElementsByTagName("GrpDepQuestID");
                    Element line = (Element) GrpDepQuestIDNode.item(0);
                    if (GrpDepQuestIDNode.getLength() > 0) {
                        grpDepQstId = Integer.valueOf(xmlParser.getCharacterDataFromElement(line));
                    }
                }
	         	              /*   int qstId=0;
		         	            	int depQstId=0;
		         	            	int grpQstId=0;
		         	            	int grpDepQstId=0;*/
                dbengine.savetblPDAQuestOptionDependentMstr(qstId, depQstId, grpQstId, grpDepQstId);

            }

            NodeList tblPDAQuestOptionValuesDependentMstr = doc.getElementsByTagName("tblPDAQuestOptionValuesDependentMstr");
            for (int i = 0; i < tblPDAQuestOptionValuesDependentMstr.getLength(); i++) {
                int DepQstId = 0;
                String DepAnswValId = "0";
                int QstId = 0;
                String AnswValId = "0";
                String OptDescr = "N/A";
                int Sequence = 0;
                int GrpQuestID = 0;
                int GrpDepQuestID = 0;

                Element element = (Element) tblPDAQuestOptionValuesDependentMstr.item(i);


                if (!element.getElementsByTagName("DepQstId").equals(null)) {
                    NodeList DepQstIdNode = element.getElementsByTagName("DepQstId");
                    Element line = (Element) DepQstIdNode.item(0);
                    if (DepQstIdNode.getLength() > 0) {
                        DepQstId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                if (!element.getElementsByTagName("DepOptID").equals(null)) {
                    NodeList DepAnswValIdNode = element.getElementsByTagName("DepOptID");
                    Element line = (Element) DepAnswValIdNode.item(0);
                    if (DepAnswValIdNode.getLength() > 0) {
                        DepAnswValId = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("QstId").equals(null)) {
                    NodeList QstIdNode = element.getElementsByTagName("QstId");
                    Element line = (Element) QstIdNode.item(0);
                    if (QstIdNode.getLength() > 0) {
                        QstId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("OptID").equals(null)) {
                    NodeList AnswValIdNode = element.getElementsByTagName("OptID");
                    Element line = (Element) AnswValIdNode.item(0);
                    if (AnswValIdNode.getLength() > 0) {
                        AnswValId = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("OptDescr").equals(null)) {
                    NodeList OptDescrNode = element.getElementsByTagName("OptDescr");
                    Element line = (Element) OptDescrNode.item(0);
                    if (OptDescrNode.getLength() > 0) {
                        OptDescr = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("Sequence").equals(null)) {
                    NodeList AnswValIdNode = element.getElementsByTagName("Sequence");
                    Element line = (Element) AnswValIdNode.item(0);
                    if (AnswValIdNode.getLength() > 0) {
                        Sequence = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                if (!element.getElementsByTagName("GrpQuestID").equals(null)) {
                    NodeList GrpQuestIDNode = element.getElementsByTagName("GrpQuestID");
                    Element line = (Element) GrpQuestIDNode.item(0);
                    if (GrpQuestIDNode.getLength() > 0) {
                        GrpQuestID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                if (!element.getElementsByTagName("GrpDepQuestID").equals(null)) {
                    NodeList GrpDepQuestIDNode = element.getElementsByTagName("GrpDepQuestID");
                    Element line = (Element) GrpDepQuestIDNode.item(0);
                    if (GrpDepQuestIDNode.getLength() > 0) {
                        GrpDepQuestID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                dbengine.savetblPDAQuestOptionValuesDependentMstr(DepQstId, DepAnswValId, QstId, AnswValId, OptDescr, Sequence, GrpQuestID, GrpDepQuestID);

            }


            NodeList tblGetPDARsnRtrnMstr = doc.getElementsByTagName("tblGetReturnsReasonForPDAMstr");
            for (int i = 0; i < tblGetPDARsnRtrnMstr.getLength(); i++) {


                String stockStatusId = "0";
                String stockStatus = "0";


                Element element = (Element) tblGetPDARsnRtrnMstr.item(i);


                if (!element.getElementsByTagName("StockStatusId").equals(null)) {
                    NodeList QuestionIDNode = element.getElementsByTagName("StockStatusId");
                    Element line = (Element) QuestionIDNode.item(0);
                    if (QuestionIDNode.getLength() > 0) {
                        stockStatusId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("StockStatus").equals(null)) {
                    NodeList OptionIDNode = element.getElementsByTagName("StockStatus");
                    Element line = (Element) OptionIDNode.item(0);
                    if (OptionIDNode.getLength() > 0) {
                        stockStatus = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.fnInsertTBLReturnRsn(stockStatusId, stockStatus);
            }


            NodeList tblOutletChannelBusinessSegmentMaster = doc.getElementsByTagName("tblOutletChannelBusinessSegmentMaster");
            for (int i = 0; i < tblOutletChannelBusinessSegmentMaster.getLength(); i++) {
                int OutChannelID = 0;
                String ChannelName = "NA";
                int BusinessSegmentID = 0;
                String BusinessSegment = "NA";
                Element element = (Element) tblOutletChannelBusinessSegmentMaster.item(i);


                if (!element.getElementsByTagName("OutChannelID").equals(null)) {
                    NodeList OutChannelIDNode = element.getElementsByTagName("OutChannelID");
                    Element line = (Element) OutChannelIDNode.item(0);
                    if (OutChannelIDNode.getLength() > 0) {
                        OutChannelID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }
                if (!element.getElementsByTagName("ChannelName").equals(null)) {
                    NodeList ChannelNameNode = element.getElementsByTagName("ChannelName");
                    Element line = (Element) ChannelNameNode.item(0);
                    if (ChannelNameNode.getLength() > 0) {
                        ChannelName = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("BusinessSegmentID").equals(null)) {
                    NodeList BusinessSegmentIDNode = element.getElementsByTagName("BusinessSegmentID");
                    Element line = (Element) BusinessSegmentIDNode.item(0);
                    if (BusinessSegmentIDNode.getLength() > 0) {
                        BusinessSegmentID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("BusinessSegment").equals(null)) {
                    NodeList BusinessSegmentNode = element.getElementsByTagName("BusinessSegment");
                    Element line = (Element) BusinessSegmentNode.item(0);
                    if (BusinessSegmentNode.getLength() > 0) {
                        BusinessSegment = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblOutletChannelBusinessSegmentMaster(OutChannelID, ChannelName, BusinessSegmentID, BusinessSegment);

            }

            setmovie.director = "1";
            dbengine.close();
            return setmovie;

        } catch (Exception e) {
            dbengine.close();
            System.out.println("Aman Exception occur in fnSingleCallAllWebService :" + e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();


            return setmovie;
        }
    }

    public ServiceWorker fnGetDistributorTodaysStock(Context ctx, int CustomerNodeID, int CustomerNodeType, String bydate, String IMEINo, String SysDate, int AppVersionID) {
        this.context = ctx;


        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnGetDistributorTodaysStock";
        final String METHOD_NAME = "fnGetDistributorTodaysStock";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        //Create request
        SoapObject table = null; //Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; //Its the client petition to the web service
        SoapObject tableRow = null; //Contains row of table
        SoapObject responseBody = null; //Contains XML content of dataset

        //DBHelper dbengine=new DBHelper(ctx);
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;
        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 0);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);
            //client.addProperty("ApplicationID", ApplicationID);
            //client.addProperty("uuid", uuid.toString());

            //String rID=dbengine.GetActiveRouteIDSunilAgain();


            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            client.addProperty("CustomerNodeID", CustomerNodeID);
            client.addProperty("CustomerNodeType", CustomerNodeType);

            client.addProperty("bydate", bydate.toString());
            client.addProperty("IMEINo", IMEINo.toString());
            //client.addProperty("rID", rID.toString());
            client.addProperty("SysDate", SysDate.toString());
            client.addProperty("AppVersionID", dbengine.AppVersionID.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);

            //  dbengine.deleteDistributorStockTbles();

            NodeList tblDistributorDayReportNode = doc.getElementsByTagName("tblDistributorDayReport");
            for (int i = 0; i < tblDistributorDayReportNode.getLength(); i++) {
                String ProductNodeID = "0";
                String ProductNodeType = "0";
                String SKUName = "0";
                String FlvShortName = "0";
                String StockDate = "0";

                Element element = (Element) tblDistributorDayReportNode.item(i);

                if (!element.getElementsByTagName("ProductNodeID").equals(null)) {
                    NodeList ProductNodeIDNode = element.getElementsByTagName("ProductNodeID");
                    Element line = (Element) ProductNodeIDNode.item(0);
                    if (ProductNodeIDNode.getLength() > 0) {
                        ProductNodeID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ProductNodeType").equals(null)) {
                    NodeList ProductNodeTypeNode = element.getElementsByTagName("ProductNodeType");
                    Element line = (Element) ProductNodeTypeNode.item(0);
                    if (ProductNodeTypeNode.getLength() > 0) {
                        ProductNodeType = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("SKUName").equals(null)) {
                    NodeList SKUNameNode = element.getElementsByTagName("SKUName");
                    Element line = (Element) SKUNameNode.item(0);
                    if (SKUNameNode.getLength() > 0) {
                        SKUName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("FlvShortName").equals(null)) {
                    NodeList FlvShortNameNode = element.getElementsByTagName("FlvShortName");
                    Element line = (Element) FlvShortNameNode.item(0);
                    if (FlvShortNameNode.getLength() > 0) {
                        FlvShortName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("StockDate").equals(null)) {
                    NodeList StockDateNode = element.getElementsByTagName("StockDate");
                    Element line = (Element) StockDateNode.item(0);
                    if (StockDateNode.getLength() > 0) {
                        StockDate = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                //AutoId= i +1;
                dbengine.savetblDistributorDayReport(ProductNodeID, ProductNodeType, SKUName, FlvShortName, StockDate, CustomerNodeID, CustomerNodeType);

            }


            NodeList tblDistributorDayReportColumnsDescNode = doc.getElementsByTagName("tblDistributorDayReportColumnsDesc");
            for (int i = 0; i < tblDistributorDayReportColumnsDescNode.getLength(); i++) {
                String DistDayReportCoumnName = "0";
                String DistDayReportColumnDisplayName = "0";

                Element element = (Element) tblDistributorDayReportColumnsDescNode.item(i);

                if (!element.getElementsByTagName("DistDayReportCoumnName").equals(null)) {
                    NodeList DistDayReportCoumnNameNode = element.getElementsByTagName("DistDayReportCoumnName");
                    Element line = (Element) DistDayReportCoumnNameNode.item(0);
                    if (DistDayReportCoumnNameNode.getLength() > 0) {
                        DistDayReportCoumnName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("DistDayReportColumnDisplayName").equals(null)) {
                    NodeList DistDayReportColumnDisplayNameNode = element.getElementsByTagName("DistDayReportColumnDisplayName");
                    Element line = (Element) DistDayReportColumnDisplayNameNode.item(0);
                    if (DistDayReportColumnDisplayNameNode.getLength() > 0) {
                        DistDayReportColumnDisplayName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                //AutoId= i +1;
                dbengine.savetblDistributorDayReportColumnsDesc(DistDayReportCoumnName, DistDayReportColumnDisplayName, CustomerNodeID, CustomerNodeType);
            }

            dbengine.close();
            setmovie.director = "1";

            return setmovie;

        } catch (Exception e) {
            dbengine.close();
            System.out.println("Aman Exception occur in fnDistributor :" + e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();


            return setmovie;
        }
    }


	/*public ServiceWorker callfnSingleCallAllWebService(Context ctx,int ApplicationID,String uuid)
	{
		this.context = ctx;
		DBAdapterKenya dbengine = new DBAdapterKenya(context);

		int chkTblStoreListContainsRow=1;
		StringReader read;
		InputSource inputstream;
		final String SOAP_ACTION = "http://tempuri.org/fnSingleCallAllStoreMappingMeijiTT";
		final String METHOD_NAME = "fnSingleCallAllStoreMappingMeijiTT";
		final String NAMESPACE = "http://tempuri.org/";
		final String URL = UrlForWebService;

	    //Create request
		SoapObject table = null; //Contains table of dataset that returned
		// through SoapObject
		SoapObject client = null; //Its the client petition to the web service
		SoapObject tableRow = null; //Contains row of table
		SoapObject responseBody = null; //Contains XML content of dataset

		//SoapObject param
		HttpTransportSE transport = null; // That call webservice
		SoapSerializationEnvelope sse = null;
		sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		sse.dotNet = true;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,timeout);

		ServiceWorker setmovie = new ServiceWorker();
		try {
			client = new SoapObject(NAMESPACE, METHOD_NAME);
			client.addProperty("ApplicationID", ApplicationID);
			client.addProperty("uuid", uuid.toString());

			sse.setOutputSoapObject(client);
			sse.bodyOut = client;
			androidHttpTransport.call(SOAP_ACTION, sse);
			responseBody = (SoapObject)sse.bodyIn;
			// This step: get file XML
			//responseBody = (SoapObject) sse.getResponse();
			int totalCount = responseBody.getPropertyCount();

			// System.out.println("Kajol 2 :"+totalCount);
	        String resultString=androidHttpTransport.responseDump;

	        String name=responseBody.getProperty(0).toString();

	       // System.out.println("Kajol 3 :"+name);

	        XMLParser xmlParser = new XMLParser();
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            InputSource is = new InputSource();
	            is.setCharacterStream(new StringReader(name));
	            Document doc = db.parse(is);



	          //  <tblSPGetDistributorDetails> <NodeID>1</NodeID> <Descr>SUDARSAN TRADERS</Descr> <Code>101338</Code> <PNodeID>8</PNodeID> </tblSPGetDistributorDetails>
	           dbengine.deleteAllSingleCallWebServiceTable();
	           dbengine.open();

	            NodeList tblSPGetDistributorDetailsNode = doc.getElementsByTagName("tblGetPDAQuestMstr");
	            for (int i = 0; i < tblSPGetDistributorDetailsNode.getLength(); i++)
	            {
	            	String QuestID="0";
	            	String QuestCode="0";
	            	String QuestDesc="0";
	            	String QuestType="0";

	            	String AnsControlType="0";
	            	String AsnControlInputTypeID="0";
	            	String AnsControlInputTypeMaxLength="0";
	            	String AnsControlInputTypeMinLength="0";
	            	String AnsMustRequiredFlg="0";
	            	String QuestBundleFlg="0";
	            	String ApplicationTypeID="0";
	            	String Sequence="0";
	            	String answerHint="N/A";

	                Element element = (Element) tblSPGetDistributorDetailsNode.item(i);




	                NodeList NodeIDNode = element.getElementsByTagName("QuestID");
	                Element line = (Element) NodeIDNode.item(0);
	                QuestID=xmlParser.getCharacterDataFromElement(line);

	                if(!element.getElementsByTagName("QuestID").equals(null))
	                 {
	                 NodeList QuestIDNode = element.getElementsByTagName("QuestID");
	                 Element     line = (Element) QuestIDNode.item(0);
		                if(QuestIDNode.getLength()>0)
		                {
		                	QuestID=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }

	                NodeList DescrNode = element.getElementsByTagName("QuestCode");
	                line = (Element) DescrNode.item(0);
	                QuestCode=xmlParser.getCharacterDataFromElement(line);

	                  if(!element.getElementsByTagName("QuestCode").equals(null))
	                 {
	                 NodeList QuestCodeNode = element.getElementsByTagName("QuestCode");
	                 Element     line = (Element) QuestCodeNode.item(0);
		                if(QuestCodeNode.getLength()>0)
		                {
		                	QuestCode=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }



	                NodeList PNodeIDNode = element.getElementsByTagName("QuestDesc");
	                line = (Element) PNodeIDNode.item(0);
	                QuestDesc=xmlParser.getCharacterDataFromElement(line);

	                   if(!element.getElementsByTagName("QuestDesc").equals(null))
	                 {
	                 NodeList QuestDescNode = element.getElementsByTagName("QuestDesc");
	                 Element     line = (Element) QuestDescNode.item(0);
		                if(QuestDescNode.getLength()>0)
		                {
		                	QuestDesc=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }


	                NodeList QuesTypeNode = element.getElementsByTagName("QuestType");
	                line = (Element) QuesTypeNode.item(0);
	                QuestType=xmlParser.getCharacterDataFromElement(line);

	                  if(!element.getElementsByTagName("QuestType").equals(null))
	                 {
	                 NodeList QuesTypeNode = element.getElementsByTagName("QuestType");
	                 Element     line = (Element) QuesTypeNode.item(0);
		                if(QuesTypeNode.getLength()>0)
		                {
		                	QuestType=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }

	                NodeList AnsControlTypeNode = element.getElementsByTagName("AnsControlType");
	                line = (Element) AnsControlTypeNode.item(0);
	                AnsControlType=xmlParser.getCharacterDataFromElement(line);

	                  if(!element.getElementsByTagName("AnsControlType").equals(null))
	                 {
	                 NodeList AnsControlTypeNode = element.getElementsByTagName("AnsControlType");
	                 Element     line = (Element) AnsControlTypeNode.item(0);
		                if(AnsControlTypeNode.getLength()>0)
		                {
		                	AnsControlType=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }


	               NodeList AsnControlInputTypeIDNode = element.getElementsByTagName("AsnControlInputTypeID");
	                line = (Element) AsnControlInputTypeIDNode.item(0);
	                AsnControlInputTypeID=xmlParser.getCharacterDataFromElement(line);

	                   if(!element.getElementsByTagName("AsnControlInputTypeID").equals(null))
	                 {
	                 NodeList AsnControlInputTypeIDNode = element.getElementsByTagName("AsnControlInputTypeID");
	                 Element     line = (Element) AsnControlInputTypeIDNode.item(0);
		                if(AsnControlInputTypeIDNode.getLength()>0)
		                {
		                	AsnControlInputTypeID=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }

	                NodeList AnsControlInputTypeMaxLengthNode = element.getElementsByTagName("AnsControlInputTypeMaxLength");
	                line = (Element) AnsControlInputTypeMaxLengthNode.item(0);
	                AnsControlInputTypeMaxLength=xmlParser.getCharacterDataFromElement(line);

	                //AnsControlInputTypeMinLength

	                    if(!element.getElementsByTagName("AnsControlInputTypeMaxLength").equals(null))
	                 {
	                 NodeList AnsControlInputTypeMaxLengthNode = element.getElementsByTagName("AnsControlInputTypeMaxLength");
	                 Element      line = (Element) AnsControlInputTypeMaxLengthNode.item(0);
		                if(AnsControlInputTypeMaxLengthNode.getLength()>0)
		                {
		                	AnsControlInputTypeMaxLength=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }


	                   if(!element.getElementsByTagName("AnsControlIntputTypeMinLength").equals(null))
	                 {
	                 NodeList AnsControlInputTypeMinLengthNode = element.getElementsByTagName("AnsControlIntputTypeMinLength");
	                 Element      line = (Element) AnsControlInputTypeMinLengthNode.item(0);
		                if(AnsControlInputTypeMinLengthNode.getLength()>0)
		                {
		                	AnsControlInputTypeMinLength=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }


	                NodeList AnsMustRequiredFlgNode = element.getElementsByTagName("AnsMustRequiredFlg");
	                line = (Element) AnsMustRequiredFlgNode.item(0);
	                AnsMustRequiredFlg=xmlParser.getCharacterDataFromElement(line);

	                   if(!element.getElementsByTagName("AnsMustRequiredFlg").equals(null))
	                 {
	                 NodeList AnsMustRequiredFlgNode = element.getElementsByTagName("AnsMustRequiredFlg");
	                 Element      line = (Element) AnsMustRequiredFlgNode.item(0);
		                if(AnsMustRequiredFlgNode.getLength()>0)
		                {
		                	AnsMustRequiredFlg=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }

	                NodeList QuestBundleFlgNode = element.getElementsByTagName("QuestBundleFlg");
	                line = (Element) QuestBundleFlgNode.item(0);
	                QuestBundleFlg=xmlParser.getCharacterDataFromElement(line);

	                 if(!element.getElementsByTagName("QuestBundleFlg").equals(null))
	                 {
	                 NodeList QuestBundleFlgNode = element.getElementsByTagName("QuestBundleFlg");
	                 Element      line = (Element) QuestBundleFlgNode.item(0);
		                if(QuestBundleFlgNode.getLength()>0)
		                {
		                	QuestBundleFlg=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }

	                NodeList ApplicationTypeIDNode = element.getElementsByTagName("ApplicationTypeID");
	                line = (Element) ApplicationTypeIDNode.item(0);
	                ApplicationTypeID=xmlParser.getCharacterDataFromElement(line);

	                 if(!element.getElementsByTagName("ApplicationTypeID").equals(null))
	                 {
	                 NodeList ApplicationTypeIDNode = element.getElementsByTagName("ApplicationTypeID");
	                 Element      line = (Element) ApplicationTypeIDNode.item(0);
		                if(ApplicationTypeIDNode.getLength()>0)
		                {
		                	ApplicationTypeID=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }


	                NodeList SequenceNode = element.getElementsByTagName("Sequence");
	                line = (Element) SequenceNode.item(0);
	                Sequence=xmlParser.getCharacterDataFromElement(line);

	                 if(!element.getElementsByTagName("Sequence").equals(null))
	                 {
	                 NodeList SequenceNode = element.getElementsByTagName("Sequence");
	                 Element      line = (Element) SequenceNode.item(0);
		                if(SequenceNode.getLength()>0)
		                {
		                	Sequence=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }


	                 if(!element.getElementsByTagName("AnswerHint").equals(null))
	                 {
	                 NodeList SequenceNode = element.getElementsByTagName("AnswerHint");
	                 Element      line = (Element) SequenceNode.item(0);
		                if(SequenceNode.getLength()>0)
		                {
		                	answerHint=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }
	                //,String AnsControlInputTypeMinLength


	             dbengine.savetblQuestionMstr(QuestID, QuestCode, QuestDesc, QuestType, AnsControlType, AsnControlInputTypeID, AnsControlInputTypeMaxLength, AnsMustRequiredFlg, QuestBundleFlg, ApplicationTypeID, Sequence,AnsControlInputTypeMinLength,answerHint);


	                System.out.println("Mieji :"+answerHint+"QuestID:"+QuestID+"QuestCode:"+QuestCode+"QuestDesc:"+QuestDesc+"QuestType:"+QuestType+"AnsControlType:"+AnsControlType+"AsnControlInputTypeID:"+AsnControlInputTypeID+"AnsControlInputTypeMaxLength:"+AnsControlInputTypeMaxLength+"AnsMustRequiredFlg:"+AnsMustRequiredFlg+" QuestBundleFlg:"+ QuestBundleFlg+" ApplicationTypeID :"+ ApplicationTypeID +" Sequence :"+ Sequence  );

	            }

	            NodeList tblGetPDAQuestGrpMappingNode = doc.getElementsByTagName("tblGetPDAQuestGrpMapping");
	            for (int i = 0; i < tblGetPDAQuestGrpMappingNode.getLength(); i++)
	            {
	            	String GrpQuestID="0";
	            	String QuestID="0";
	            	String GrpID="0";
	            	String GrpNodeID="0";

	            	String GrpDesc="0";
	            	String SectionNo="0";

	                Element element = (Element) tblGetPDAQuestGrpMappingNode.item(i);




	                if(!element.getElementsByTagName("GrpQuestID").equals(null))
	                 {
	                 NodeList GrpQuestIDNode = element.getElementsByTagName("GrpQuestID");
	                 Element     line = (Element) GrpQuestIDNode.item(0);
		                if(GrpQuestIDNode.getLength()>0)
		                {
		                	GrpQuestID=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }
	                if(!element.getElementsByTagName("QuestID").equals(null))
	                 {
	                 NodeList QuestIDNode = element.getElementsByTagName("QuestID");
	                 Element     line = (Element) QuestIDNode.item(0);
		                if(QuestIDNode.getLength()>0)
		                {
		                	QuestID=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }



	                   if(!element.getElementsByTagName("GrpID").equals(null))
	                 {
	                 NodeList GrpIDNode = element.getElementsByTagName("GrpID");
	                 Element     line = (Element) GrpIDNode.item(0);
		                if(GrpIDNode.getLength()>0)
		                {
		                	GrpID=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }

	                  if(!element.getElementsByTagName("GrpNodeID").equals(null))
	                 {
	                 NodeList GrpNodeIDNode = element.getElementsByTagName("GrpNodeID");
	                 Element     line = (Element) GrpNodeIDNode.item(0);
		                if(GrpNodeIDNode.getLength()>0)
		                {
		                	GrpNodeID=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }


	                  if(!element.getElementsByTagName("GrpDesc").equals(null))
	                 {
	                 NodeList GrpDescNode = element.getElementsByTagName("GrpDesc");
	                 Element     line = (Element) GrpDescNode.item(0);
		                if(GrpDescNode.getLength()>0)
		                {
		                	GrpDesc=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }



	                   if(!element.getElementsByTagName("SectionNo").equals(null))
	                 {
	                 NodeList SectionNoNode = element.getElementsByTagName("SectionNo");
	                 Element     line = (Element) SectionNoNode.item(0);
		                if(SectionNoNode.getLength()>0)
		                {
		                	SectionNo=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }


	             dbengine.savetblPDAQuestGrpMappingMstr(GrpQuestID, QuestID, GrpID, GrpNodeID, GrpDesc, SectionNo);

	            }



	            NodeList tblGetPDAQuestOptionMstrNode = doc.getElementsByTagName("tblGetPDAQuestOptionMstr");
	            for (int i = 0; i < tblGetPDAQuestOptionMstrNode.getLength(); i++)
	            {

	            	String OptID="0";
	            	String QuestID="0";
	            	String OptionNo="0";
	            	String OptionDescr="0";
	            	String Sequence="0";






	            	  Element element = (Element) tblGetPDAQuestOptionMstrNode.item(i);

	                if(!element.getElementsByTagName("OptID").equals(null))
	                 {
	                 NodeList OptIDNode = element.getElementsByTagName("OptID");
	                 Element      line = (Element) OptIDNode.item(0);
		                if(OptIDNode.getLength()>0)
		                {
		                	OptID=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }

	                if(!element.getElementsByTagName("QuestID").equals(null))
	                 {
	                 NodeList QuestIDNode = element.getElementsByTagName("QuestID");
	                 Element      line = (Element) QuestIDNode.item(0);
		                if(QuestIDNode.getLength()>0)
		                {
		                	QuestID=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }

	                if(!element.getElementsByTagName("AnsVal").equals(null))
	                 {
	                 NodeList OptionNoDNode = element.getElementsByTagName("AnsVal");
	                 Element      line = (Element) OptionNoDNode.item(0);
		                if(OptionNoDNode.getLength()>0)
		                {
		                	OptionNo=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }

	                if(!element.getElementsByTagName("OptionDescr").equals(null))
	                 {
	                 NodeList OptionDescrNode = element.getElementsByTagName("OptionDescr");
	                 Element      line = (Element) OptionDescrNode.item(0);
		                if(OptionDescrNode.getLength()>0)
		                {
		                	OptionDescr=xmlParser.getCharacterDataFromElement(line);
		                }
	            	 }

	                if(!element.getElementsByTagName("Sequence").equals(null))
	                 {
	                 NodeList SequenceNode = element.getElementsByTagName("Sequence");
	                 Element      line = (Element) SequenceNode.item(0);
		                if(SequenceNode.getLength()>0)
		                {

		                		Sequence=xmlParser.getCharacterDataFromElement(line);

		                }
	            	 }
	              dbengine.savetblOptionMstr(OptID, QuestID, OptionNo, OptionDescr, Sequence);
	            	System.out.println("OptID:"+OptID+"QuestID:"+QuestID+"OptionNo:"+OptionNo+"OptionDescr:"+OptionDescr  +"Sequence:"+Sequence);

	             }


	            NodeList tblGetPDAQuestionDependentMstrNode = doc.getElementsByTagName("tblGetPDAQuestionDependentMstr");
	                        for (int i = 0; i < tblGetPDAQuestionDependentMstrNode.getLength(); i++)
	                        {


	                         String QuestionID="0";
	                         String OptionID="0";
	                         String DependentQuestionID="0";







	                           Element element = (Element) tblGetPDAQuestionDependentMstrNode.item(i);



	                            if(!element.getElementsByTagName("QuestID").equals(null))
	                             {
	                             NodeList QuestionIDNode = element.getElementsByTagName("QuestID");
	                             Element      line = (Element) QuestionIDNode.item(0);
	                             if(QuestionIDNode.getLength()>0)
	                             {
	                              QuestionID=xmlParser.getCharacterDataFromElement(line);
	                             }
	                          }

	                            if(!element.getElementsByTagName("OptionID").equals(null))
	                             {
	                             NodeList OptionIDNode = element.getElementsByTagName("OptionID");
	                             Element      line = (Element) OptionIDNode.item(0);
	                             if(OptionIDNode.getLength()>0)
	                             {
	                              OptionID=xmlParser.getCharacterDataFromElement(line);
	                             }
	                          }

	                            if(!element.getElementsByTagName("DependentQuestionID").equals(null))
	                             {
	                             NodeList DependentQuestionIDNode = element.getElementsByTagName("DependentQuestionID");
	                             Element      line = (Element) DependentQuestionIDNode.item(0);
	                             if(DependentQuestionIDNode.getLength()>0)
	                             {
	                              DependentQuestionID=xmlParser.getCharacterDataFromElement(line);
	                             }
	                          }
	                           dbengine.savetblQuestionDependentMstr(QuestionID, OptionID, DependentQuestionID);

	                        //  dbengine.savetblOptionMstr(OptID, QuestID, OptionNo, OptionDescr, Sequence);
	                         System.out.println("QuestionID:"+QuestionID+"OptionID:"+OptionID+"DependentQuestionID:"+DependentQuestionID);

	                         }


            setmovie.director = "1";

			return setmovie;

		} catch (Exception e)
		{
			 dbengine.close();
			System.out.println("Aman Exception occur in fnSingleCallAllWebService :"+e.toString());
			setmovie.director = e.toString();
			setmovie.movie_name = e.toString();


			return setmovie;
		}
	}*/
    // Working for Parag

    public ServiceWorker fnGetStoreListWithPaymentAddressMR(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        String RouteType = "0";


        DBAdapterKenya dbengine = new DBAdapterKenya(context);

        try {
            dbengine.open();
            String RouteID = dbengine.GetActiveRouteID();
            RouteType = dbengine.FetchRouteType(rID);
            dbengine.close();
            System.out.println("hi" + RouteType);
        } catch (Exception e) {
            System.out.println("error" + e);
        }


        dbengine.open();


        final String SOAP_ACTION = "http://tempuri.org/GetStoreListWithPaymentAddressMR";
        final String METHOD_NAME = "GetStoreListWithPaymentAddressMR";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service
        decimalFormat.applyPattern(pattern);
        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME


        //String RouteType=dbengine.FetchRouteType();


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {


            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";
            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            // System.out.println("Aman Exception occur value bydate"+ dateVAL.toString());
            // System.out.println("Aman Exception occur value IMEINo"+ uuid.toString());
            // System.out.println("Aman Exception occur value rID"+ rID.toString());
            // System.out.println("Aman Exception occur value SysDate"+ SysDate.toString());
            // System.out.println("Aman Exception occur value AppVersionID"+ dbengine.AppVersionID.toString());

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteType", RouteType);
            client.addProperty("SysDate", SysDate.toString());
            client.addProperty("AppVersionID", dbengine.AppVersionID.toString());

            //dbengine.AppVersionID.toString()

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);


            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();


            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }


            int chkTblStoreContainsRow = 0;


            if (soDiffg.hasProperty("NewDataSet")) {
                chkTblStoreContainsRow = 1;
                dbengine.fndeleteStoreAddressMapDetailsMstr();
            }

            if (chkTblStoreContainsRow == 1) {

                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


                    //////// System.out.println("SOAP_STORE ID: " + tableRow.getProperty("StoreID").toString());
                    //////// System.out.println("SOAP_2_STORE ID: " + tableRow.getProperty("StoreID"));
                    String StoreID = "0";
                    int OutAddTypeID = 0;

                    String Address = "";
                    String AddressDet = "Not Available";
                    int OutAddID = 0;

                    if (tableRow.hasProperty("StoreID")) {
                        //////// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                        if (tableRow.getProperty("StoreID").toString().isEmpty()) {
                            StoreID = "0";
                        } else {
                            StoreID = tableRow.getProperty("StoreID").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("OutAddTypeID")) {
                        //////// System.out.println("OutAddTypeID id property exist :"+tableRow.hasProperty("OutAddTypeID"));
                        if (tableRow.getProperty("OutAddTypeID").toString().isEmpty()) {
                            OutAddTypeID = 0;
                        } else {
                            OutAddTypeID = Integer.parseInt(tableRow.getProperty("OutAddTypeID").toString().trim());

                        }
                    }
                    if (tableRow.hasProperty("Address")) {
                        //////// System.out.println("Address id property exist :"+tableRow.hasProperty("Address"));
                        if (tableRow.getProperty("Address").toString().isEmpty()) {
                            Address = "Not Available";
                        } else {
                            Address = tableRow.getProperty("Address").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("AddressDet")) {
                        //////// System.out.println("AddressDet id property exist :"+tableRow.hasProperty("AddressDet"));
                        if (tableRow.getProperty("AddressDet").toString().isEmpty()) {
                            AddressDet = "Not Available";
                        } else {
                            AddressDet = tableRow.getProperty("AddressDet").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("OutAddID")) {
                        //////// System.out.println("OutAddID id property exist :"+tableRow.hasProperty("OutAddID"));
                        if (tableRow.getProperty("OutAddID").toString().isEmpty()) {
                            OutAddID = 0;
                        } else {
                            OutAddID = Integer.parseInt(tableRow.getProperty("OutAddID").toString().trim());

                        }
                    }


                    int AutoIdStore = 0;
                    AutoIdStore = i + 1;

                    dbengine.saveSOAPdataStoreListAddressMap(StoreID, OutAddTypeID, Address, AddressDet, OutAddID);


                }
            }

            dbengine.close();


            setmovie.director = "1";
            // System.out.println("ServiceWorkerNitish getallStores Completed ");
            flagExecutedServiceSuccesfully = 1;
            return setmovie;

        } catch (Exception e) {
            // System.out.println("Aman Exception occur in GetStoreListMR :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }


    }


    public ServiceWorker getallProduct(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetProductListMRNew";//GetProductListMRNewProductFilterTest";
        final String METHOD_NAME = "GetProductListMRNew";//GetProductListMRNewProductFilterTest
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        decimalFormat.applyPattern(pattern);
        SoapObject table = null; // Contains table of dataset that returned
        // throug SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        //sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);


        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);

            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);

			/*client.addProperty("bydate", dateVAL.toString());
			client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;
            String resultString = androidHttpTransport.responseDump;
            String name = responseBody.getProperty(0).toString();
            // This step: get file XML
			/*responseBody = (SoapObject) sse.getResponse();
			  String name=responseBody.getProperty(0).toString();*/

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);

            dbengine.Delete_tblProductList_for_refreshData();
            NodeList tblPrdctMstrNode = doc.getElementsByTagName("tblProductListMaster");
            for (int i = 0; i < tblPrdctMstrNode.getLength(); i++)

            {
                String CatID = "0";
                String ProductID = "0";
                String ProductShortName = "NA";
                String DisplayUnit = "0";
                Double CalculateKilo = 0.0;
                String KGLiter = "0";
                int StoreCatNodeId = 0;
                String SearchField = "";

                int CatOrdr = 0;
                int PrdOrdr = 0;
                int ManufacturerID = 0;
                Element element = (Element) tblPrdctMstrNode.item(i);

                if (!element.getElementsByTagName("CatID").equals(null)) {

                    NodeList CatIDNode = element.getElementsByTagName("CatID");
                    Element line = (Element) CatIDNode.item(0);

                    if (CatIDNode.getLength() > 0) {

                        CatID = xmlParser.getCharacterDataFromElement(line);
                        StoreCatNodeId = Integer.parseInt(CatID);
                    }
                }
                if (!element.getElementsByTagName("ProductID").equals(null)) {

                    NodeList ProductIDNode = element.getElementsByTagName("ProductID");
                    Element line = (Element) ProductIDNode.item(0);

                    if (ProductIDNode.getLength() > 0) {

                        ProductID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ProductShortName").equals(null)) {

                    NodeList ProductShortNameNode = element.getElementsByTagName("ProductShortName");
                    Element line = (Element) ProductShortNameNode.item(0);

                    if (ProductShortNameNode.getLength() > 0) {

                        ProductShortName = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("DisplayUnit").equals(null)) {

                    NodeList DisplayUnitNode = element.getElementsByTagName("DisplayUnit");
                    Element line = (Element) DisplayUnitNode.item(0);

                    if (DisplayUnitNode.getLength() > 0) {

                        DisplayUnit = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("CalculateKilo").equals(null)) {

                    NodeList CalculateKiloNode = element.getElementsByTagName("CalculateKilo");
                    Element line = (Element) CalculateKiloNode.item(0);

                    if (CalculateKiloNode.getLength() > 0) {

                        CalculateKilo = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("KGLiter").equals(null)) {

                    NodeList KGLiterNode = element.getElementsByTagName("KGLiter");
                    Element line = (Element) KGLiterNode.item(0);

                    if (KGLiterNode.getLength() > 0) {

                        KGLiter = xmlParser.getCharacterDataFromElement(line);

                    }
                }

                if (!element.getElementsByTagName("CatOrdr").equals(null)) {

                    NodeList CatOrdrNode = element.getElementsByTagName("CatOrdr");
                    Element line = (Element) CatOrdrNode.item(0);

                    if (CatOrdrNode.getLength() > 0) {

                        CatOrdr = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                    }
                }

                if (!element.getElementsByTagName("PrdOrdr").equals(null)) {

                    NodeList PrdOrdrNode = element.getElementsByTagName("PrdOrdr");
                    Element line = (Element) PrdOrdrNode.item(0);

                    if (PrdOrdrNode.getLength() > 0) {

                        PrdOrdr = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                    }
                }
                if (!element.getElementsByTagName("StoreCatNodeId").equals(null)) {

                    NodeList StoreCatNodeIdNode = element.getElementsByTagName("StoreCatNodeId");
                    Element line = (Element) StoreCatNodeIdNode.item(0);

                    if (StoreCatNodeIdNode.getLength() > 0) {

                        StoreCatNodeId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                    }
                }

                if (!element.getElementsByTagName("SearchField").equals(null)) {

                    NodeList SearchFieldNode = element.getElementsByTagName("SearchField");
                    Element line = (Element) SearchFieldNode.item(0);

                    if (SearchFieldNode.getLength() > 0) {

                        SearchField = xmlParser.getCharacterDataFromElement(line);

                    }
                }


                if (!element.getElementsByTagName("ManufacturerID").equals(null)) {

                    NodeList ManufacturerIDNode = element.getElementsByTagName("ManufacturerID");
                    Element line = (Element) ManufacturerIDNode.item(0);

                    if (ManufacturerIDNode.getLength() > 0) {

                        ManufacturerID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                    }
                }


                //SearchField
                dbengine.saveSOAPdataProductList(CatID, ProductID, ProductShortName, DisplayUnit, CalculateKilo, KGLiter, CatOrdr, PrdOrdr, StoreCatNodeId, SearchField, ManufacturerID);


            }


            NodeList tblProductSegementMap = doc.getElementsByTagName("tblProductSegementMap");
            for (int i = 0; i < tblProductSegementMap.getLength(); i++) {

                String ProductID = "0";
                int BusinessSegmentId = 0;
                Double ProductMRP = 0.0;
                Double ProductRLP = 0.0;
                Double ProductTaxAmount = 0.0;
                Double RetMarginPer = 0.0;
                Double VatTax = 0.0;
                Double StandardRate = 0.0;
                Double StandardRateBeforeTax = 0.0;
                Double StandardTax = 0.0;
                int flgPriceAva = 0;


                Element element = (Element) tblProductSegementMap.item(i);

                if (!element.getElementsByTagName("ProductId").equals(null)) {

                    NodeList ProductIDNode = element.getElementsByTagName("ProductId");
                    Element line = (Element) ProductIDNode.item(0);

                    if (ProductIDNode.getLength() > 0) {

                        ProductID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("ProductMRP").equals(null)) {

                    NodeList ProductMRPNode = element.getElementsByTagName("ProductMRP");
                    Element line = (Element) ProductMRPNode.item(0);

                    if (ProductMRPNode.getLength() > 0) {

                        ProductMRP = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));
                        ProductMRP = Double.parseDouble(decimalFormat.format(ProductMRP));
                    }
                }

                if (!element.getElementsByTagName("ProductRLP").equals(null)) {

                    NodeList ProductRLPNode = element.getElementsByTagName("ProductRLP");
                    Element line = (Element) ProductRLPNode.item(0);

                    if (ProductRLPNode.getLength() > 0) {

                        ProductRLP = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));
                        ProductRLP = Double.parseDouble(decimalFormat.format(ProductRLP));
                    }
                }

                if (!element.getElementsByTagName("ProductTaxAmount").equals(null)) {

                    NodeList ProductTaxAmountNode = element.getElementsByTagName("ProductTaxAmount");
                    Element line = (Element) ProductTaxAmountNode.item(0);

                    if (ProductTaxAmountNode.getLength() > 0) {

                        ProductTaxAmount = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));
                        ProductTaxAmount = Double.parseDouble(decimalFormat.format(ProductTaxAmount));
                    }
                }


                if (!element.getElementsByTagName("RetMarginPer").equals(null)) {

                    NodeList RetMarginPerNode = element.getElementsByTagName("RetMarginPer");
                    Element line = (Element) RetMarginPerNode.item(0);

                    if (RetMarginPerNode.getLength() > 0) {

                        RetMarginPer = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));

                    }
                }

                if (!element.getElementsByTagName("Tax").equals(null)) {

                    NodeList TaxNode = element.getElementsByTagName("Tax");
                    Element line = (Element) TaxNode.item(0);

                    if (TaxNode.getLength() > 0) {

                        VatTax = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));

                    }
                }


                if (!element.getElementsByTagName("StandardRate").equals(null)) {

                    NodeList StandardRateNode = element.getElementsByTagName("StandardRate");
                    Element line = (Element) StandardRateNode.item(0);

                    if (StandardRateNode.getLength() > 0) {

                        StandardRate = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));

                    }
                }
                if (!element.getElementsByTagName("StandardRateBeforeTax").equals(null)) {

                    NodeList StandardRateBeforeTaxNode = element.getElementsByTagName("StandardRateBeforeTax");
                    Element line = (Element) StandardRateBeforeTaxNode.item(0);

                    if (StandardRateBeforeTaxNode.getLength() > 0) {

                        StandardRateBeforeTax = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));

                    }
                }
                if (!element.getElementsByTagName("StandardTax").equals(null)) {

                    NodeList StandardTaxNode = element.getElementsByTagName("StandardTax");
                    Element line = (Element) StandardTaxNode.item(0);

                    if (StandardTaxNode.getLength() > 0) {

                        StandardTax = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));

                    }
                }


                if (!element.getElementsByTagName("BusinessSegmentId").equals(null)) {

                    NodeList BusinessSegmentIdNode = element.getElementsByTagName("BusinessSegmentId");
                    Element line = (Element) BusinessSegmentIdNode.item(0);

                    if (BusinessSegmentIdNode.getLength() > 0) {

                        BusinessSegmentId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                    }
                }

                if (!element.getElementsByTagName("flgPriceAva").equals(null)) {

                    NodeList flgPriceAvaNode = element.getElementsByTagName("flgPriceAva");
                    Element line = (Element) flgPriceAvaNode.item(0);

                    if (flgPriceAvaNode.getLength() > 0) {

                        flgPriceAva = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                    }
                }
                //flgPriceAva

                dbengine.saveProductSegementMap(ProductID, ProductMRP, ProductRLP, ProductTaxAmount, RetMarginPer, VatTax, StandardRate, StandardRateBeforeTax, StandardTax, BusinessSegmentId, flgPriceAva);


            }


            dbengine.close();

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 2;
            // System.out.println("ServiceWorkerNitish getallProduct Inside");
            return setmovie;
//return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetProductListMRNew :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    /*public ServiceWorker getallProduct(Context ctx, String dateVAL, String uuid, String rID)
	{
		this.context = ctx;

		DBAdapterKenya dbengine = new DBAdapterKenya(context);
		dbengine.open();

		final String SOAP_ACTION = "http://tempuri.org/GetProductListMRNew";//GetProductListMRNewProductFilterTest";
		final String METHOD_NAME = "GetProductListMRNew";//GetProductListMRNewProductFilterTest
		final String NAMESPACE = "http://tempuri.org/";
		final String URL = UrlForWebService;

		decimalFormat.applyPattern(pattern);
		SoapObject table = null; // Contains table of dataset that returned
									// throug SoapObject
		SoapObject client = null; // Its the client petition to the web service
		SoapObject tableRow = null; // Contains row of table
		SoapObject responseBody = null; // Contains XML content of dataset

		//SoapObject param
		HttpTransportSE transport = null; // That call webservice
		SoapSerializationEnvelope sse = null;



		sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
		// Note if class name isn't "movie" ,you must change
		sse.dotNet = true; // if WebService written .Net is result=true
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				URL,timeout);


		ServiceWorker setmovie = new ServiceWorker();
		try {
			client = new SoapObject(NAMESPACE, METHOD_NAME);

			//String dateVAL = "00.00.0000";

			//////// System.out.println("soap obj date: "+ dateVAL);

			client.addProperty("IMEINo", uuid.toString());
			client.addProperty("rID", rID.toString());

			client.addProperty("bydate", dateVAL.toString());
			client.addProperty("IMEINo", uuid.toString());

			sse.setOutputSoapObject(client);
			sse.bodyOut = client;
			androidHttpTransport.call(SOAP_ACTION, sse);

			// This step: get file XML
			responseBody = (SoapObject) sse.getResponse();


			  SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

	            if(soDiffg.hasProperty("NewDataSet"))
	            {
	            	// remove information XML,only retrieved results that returned
	    			responseBody = (SoapObject) responseBody.getProperty(1);

	    			// get information XMl of tables that is returned
	    			table = (SoapObject) responseBody.getProperty(0);
	            }
	            else
	            {

	            }

			int chkTblProductListContainsRow=0;

			if(soDiffg.hasProperty("NewDataSet"))
			{
				chkTblProductListContainsRow=1;
				dbengine.Delete_tblProductList_for_refreshData();
			}

			if(chkTblProductListContainsRow==1)
			{
				if( table.getPropertyCount()>1)
				{
					for(int i = 0; i <= table.getPropertyCount() -1 ; i++)
					{

					   // ////// System.out.println("table - prop count: "+ table.getPropertyCount());
						tableRow = (SoapObject) table.getProperty(i);
						//////// System.out.println("i value: "+ i);

						<xs:element name="CatID" minOccurs="0" type="xs:int"/>

						<xs:element name="ProductMRP" minOccurs="0" type="xs:decimal"/>

						<xs:element name="ProductRLP" minOccurs="0" type="xs:decimal"/>

						<xs:element name="ProductTaxAmount" minOccurs="0" type="xs:int"/>

						<xs:element name="KGLiter" minOccurs="0" type="xs:int"/>

						String CatID="0";
						String ProductID="0";
						String ProductShortName="NA";
						String DisplayUnit="0";
						Double CalculateKilo=0.0;
						Double ProductMRP=0.0;
						Double ProductRLP=0.0;
						Double ProductTaxAmount=0.0;
						String KGLiter="0";
						Double RetMarginPer=0.0;
						Double VatTax=0.0;
						Double StandardRate=0.0;
						Double StandardRateBeforeTax=0.0;
						Double StandardTax=0.0;
						int StoreCatNodeId=0;
						String SearchField="";
						int CatOrdr=0;
						int PrdOrdr=0;

						if (tableRow.hasProperty("CatID") )
						{
							if (tableRow.getProperty("CatID").toString().isEmpty() )
							{
								CatID="0";
							}
							else
							{
								CatID = tableRow.getProperty("CatID").toString().trim();

							}
						}
						if (tableRow.hasProperty("ProductID") )
						{
							if (tableRow.getProperty("ProductID").toString().isEmpty() )
							{
								ProductID="0";
							}
							else
							{
								ProductID = tableRow.getProperty("ProductID").toString().trim();

							}
						}
						if (tableRow.hasProperty("ProductShortName") )
						{
							if (tableRow.getProperty("ProductShortName").toString().isEmpty() )
							{
								ProductShortName="0";
							}
							else
							{
								ProductShortName = tableRow.getProperty("ProductShortName").toString().trim();

							}
						}
						if (tableRow.hasProperty("DisplayUnit") )
						{
							if (tableRow.getProperty("DisplayUnit").toString().isEmpty() )
							{
								DisplayUnit="0";
							}
							else
							{
								DisplayUnit = tableRow.getProperty("DisplayUnit").toString().trim();

							}
						}
						if (tableRow.hasProperty("CalculateKilo") )
						{
							if (tableRow.getProperty("CalculateKilo").toString().isEmpty() )
							{
								CalculateKilo=0.0;
							}
							else
							{
								CalculateKilo = Double.parseDouble(tableRow.getProperty("CalculateKilo").toString().trim());
										//Double.parseDouble(tableRow.getProperty("CalculateKilo").toString().trim());

							}
						}
						if (tableRow.hasProperty("ProductMRP") )
						{
							if (tableRow.getProperty("ProductMRP").toString().isEmpty() )
							{
								ProductMRP=0.0;
							}
							else
							{
								//ProductMRP = Double.parseDouble(decimalFormat.format(tableRow.getProperty("ProductMRP").toString().trim()));
								ProductMRP = Double.parseDouble(tableRow.getProperty("ProductMRP").toString().trim());
								ProductMRP=Double.parseDouble(decimalFormat.format(ProductMRP));
										//Double.parseDouble(tableRow.getProperty("ProductMRP").toString().trim());

							}
						}
						if (tableRow.hasProperty("ProductRLP") )
						{
							if (tableRow.getProperty("ProductRLP").toString().isEmpty() )
							{
								ProductRLP=0.0;
							}
							else
							{
								ProductRLP =Double.parseDouble(tableRow.getProperty("ProductRLP").toString().trim());
								ProductRLP=Double.parseDouble(decimalFormat.format(ProductRLP));
										//Double.parseDouble(tableRow.getProperty("ProductRLP").toString().trim());

							}
						}
						if (tableRow.hasProperty("ProductTaxAmount") )
						{
							if (tableRow.getProperty("ProductTaxAmount").toString().isEmpty() )
							{
								ProductTaxAmount=0.0;
							}
							else
							{
								ProductTaxAmount =Double.parseDouble(tableRow.getProperty("ProductTaxAmount").toString().trim());
								ProductTaxAmount=Double.parseDouble(decimalFormat.format(ProductTaxAmount));
										//Double.parseDouble(tableRow.getProperty("ProductTaxAmount").toString().trim());

							}
						}
						if (tableRow.hasProperty("KGLiter") )
						{
							if (tableRow.getProperty("KGLiter").toString().isEmpty() )
							{
								KGLiter="0";
							}
							else
							{
								KGLiter = tableRow.getProperty("KGLiter").toString().trim();

							}
						}
						if (tableRow.hasProperty("RetMarginPer") )
						{
							if (tableRow.getProperty("RetMarginPer").toString().isEmpty() )
							{
								RetMarginPer=0.0;
							}
							else
							{
								RetMarginPer =Double.parseDouble(tableRow.getProperty("RetMarginPer").toString().trim());

							}
						}
						if (tableRow.hasProperty("Tax") )
						{
							if (tableRow.getProperty("Tax").toString().isEmpty() )
							{
								VatTax=0.0;
							}
							else
							{
								VatTax =Double.parseDouble(tableRow.getProperty("Tax").toString().trim());

							}
						}
						if (tableRow.hasProperty("StandardRate") )
						{
							if (tableRow.getProperty("StandardRate").toString().isEmpty() )
							{
								StandardRate=0.0;
							}
							else
							{
								StandardRate =Double.parseDouble(tableRow.getProperty("StandardRate").toString().trim());

							}
						}
						if (tableRow.hasProperty("StandardRateBeforeTax") )
						{
							if (tableRow.getProperty("StandardRateBeforeTax").toString().isEmpty() )
							{
								StandardRateBeforeTax=0.0;
							}
							else
							{
								StandardRateBeforeTax =Double.parseDouble(tableRow.getProperty("StandardRateBeforeTax").toString().trim());

							}
						}
						if (tableRow.hasProperty("StandardTax") )
						{
							if (tableRow.getProperty("StandardTax").toString().isEmpty() )
							{
								StandardTax=0.0;
							}
							else
							{
								StandardTax =Double.parseDouble(tableRow.getProperty("StandardTax").toString().trim());

							}
						}
						if (tableRow.hasProperty("CatOrdr") )
						{
							if (tableRow.getProperty("CatOrdr").toString().isEmpty() )
							{
								CatOrdr=0;
							}
							else
							{
								CatOrdr =Integer.parseInt(tableRow.getProperty("CatOrdr").toString().trim());

							}
						}
						if (tableRow.hasProperty("PrdOrdr") )
						{
							if (tableRow.getProperty("PrdOrdr").toString().isEmpty() )
							{
								PrdOrdr=0;
							}
							else
							{
								PrdOrdr =Integer.parseInt(tableRow.getProperty("PrdOrdr").toString().trim());

							}
						}
						if (tableRow.hasProperty("StoreCatNodeId") )
						{
							if (tableRow.getProperty("StoreCatNodeId").toString().isEmpty() )
							{
								StoreCatNodeId=0;
							}
							else
							{
								StoreCatNodeId =Integer.parseInt(tableRow.getProperty("StoreCatNodeId").toString().trim());

							}
						}
						//StoreCatNodeId

						if (tableRow.hasProperty("SearchField") )
						{
							if (tableRow.getProperty("SearchField").toString().isEmpty() )
							{
								SearchField="";
							}
							else
							{
								SearchField =tableRow.getProperty("SearchField").toString().trim();

							}
						}
						//SearchField
						dbengine.saveSOAPdataProductList(CatID,ProductID,ProductShortName,DisplayUnit,CalculateKilo,ProductMRP, ProductRLP, ProductTaxAmount,KGLiter,RetMarginPer,VatTax,StandardRate,StandardRateBeforeTax,StandardTax,CatOrdr,PrdOrdr,StoreCatNodeId,SearchField);


					}
				}

			}


			dbengine.close();

			setmovie.director = "1";
			flagExecutedServiceSuccesfully=2;
			// System.out.println("ServiceWorkerNitish getallProduct Inside");
			return setmovie;
//return counts;
		} catch (Exception e) {

			// System.out.println("Aman Exception occur in GetProductListMRNew :"+e.toString());
			setmovie.director = e.toString();
			setmovie.movie_name = e.toString();
			flagExecutedServiceSuccesfully=0;
			dbengine.close();
			return setmovie;
		}

	}
	*/
    public ServiceWorker getProductPriceMR(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetForPDAProductPriceMR";
        final String METHOD_NAME = "GetForPDAProductPriceMR";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        decimalFormat.applyPattern(pattern);

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);


        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            ////// System.out.println("soap obj date: "+ dateVAL);

            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

			/*client.addProperty("bydate", dateVAL.toString());
			client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            ////// System.out.println("Debug: " + "dbengine.open");

            ////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            //dbengine.reCreateDB();
            //chkTblStoreListContainsRow
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            ////// System.out.println("chkTblStoreListContainsRow: for ProductList "+ chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
						/*StoreID text  null,ProductID text  null, ProductMRP real  null, ProductRLP real  null,
						ProductTaxAmount real  null, KGLiter text null);";
*/
                        ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        ////// System.out.println("i value: "+ i);

                        String StoreID = tableRow.getProperty("StoreID").toString().trim();


                        String mrp = tableRow.getProperty("ProductMRP").toString().trim();
                        Double valMRP = Double.parseDouble(mrp.toString().trim());
                        valMRP = Double.parseDouble(decimalFormat.format(valMRP));
                        //Double.parseDouble(mrp);

                        ////// System.out.println("ProductMRP   :"+valMRP);

                        String rlp = tableRow.getProperty("ProductRLP").toString().trim();
                        Double valRLP = Double.parseDouble(decimalFormat.format(rlp));
                        //Double.parseDouble(rlp);

                        ////// System.out.println("ProductRLP   :"+valRLP);

                        String pta = tableRow.getProperty("ProductTaxAmount").toString().trim();
                        Double valPTA = Double.parseDouble(decimalFormat.format(pta));
                        //Double.parseDouble(pta);

                        ////// System.out.println("ProductTaxAmount   :"+valPTA);

                        Double DistributorPrice = Double.parseDouble(decimalFormat.format(tableRow.getProperty("DistributorPrice").toString().trim()));
                        //Double.parseDouble(tableRow.getProperty("DistributorPrice").toString().trim());

                        int CategoryID = 0;
                        if (tableRow.hasProperty("CategoryID")) {
                            if (tableRow.getProperty("CategoryID").toString().isEmpty()) {
                                CategoryID = 0;
                            } else {
                                CategoryID = Integer.parseInt(tableRow.getProperty("CategoryID").toString().trim());
                            }
                        }


                        //dbengine.saveSOAPdataProductList(CategoryID,tableRow.getProperty("ProductID").toString().trim(), tableRow.getProperty("ProductShortName").toString().trim());
                        dbengine.saveSOAPdataStoreProductMap(StoreID, tableRow.getProperty("ProductID").toString().trim(), valMRP, valRLP, valPTA, DistributorPrice, CategoryID);


                    }
                }

            }


            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetForPDAProductPriceMR :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getallSchemeList(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetSchemeList";
        final String METHOD_NAME = "GetSchemeList";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service

        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);


            client.addProperty("bydate", dateVAL.toString());

            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");
            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
					/*<xs:element name="SchemeID" minOccurs="0" type="xs:int"/>

					<xs:element name="SchemeName" minOccurs="0" type="xs:string"/>

					<xs:element name="SchemeType" minOccurs="0" type="xs:unsignedByte"/>

					<xs:element name="StoreType" minOccurs="0" type="xs:int"/>

					<xs:element name="CombTypeID" minOccurs="0" type="xs:int"/>*/


                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);
                        String SchemeID = "0";
                        String SchemeName = "0";
                        String SchemeType = "0";
                        String StoreType = "0";
                        int CombTypeID = 0;
                        int flgDiscountType = 0;


                        if (tableRow.hasProperty("SchemeID")) {
                            //////// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("SchemeID").toString().isEmpty()) {
                                SchemeID = "0";
                            } else {
                                SchemeID = tableRow.getProperty("SchemeID").toString().trim();

                            }
                        }
                        if (tableRow.hasProperty("SchemeName")) {
                            //////// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("SchemeName").toString().isEmpty()) {
                                SchemeName = "0";
                            } else {
                                SchemeName = tableRow.getProperty("SchemeName").toString().trim();

                            }
                        }
                        if (tableRow.hasProperty("SchemeType")) {
                            //////// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("SchemeType").toString().isEmpty()) {
                                SchemeType = "0";
                            } else {
                                SchemeType = tableRow.getProperty("SchemeType").toString().trim();

                            }
                        }
                        if (tableRow.hasProperty("StoreType")) {
                            //////// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("StoreType").toString().isEmpty()) {
                                StoreType = "0";
                            } else {
                                StoreType = tableRow.getProperty("StoreType").toString().trim();

                            }
                        }
                        if (tableRow.hasProperty("CombTypeID")) {
                            //////// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("CombTypeID").toString().isEmpty()) {
                                CombTypeID = 0;
                            } else {
                                CombTypeID = Integer.parseInt(tableRow.getProperty("CombTypeID").toString().trim());

                            }
                        }
                        if (tableRow.hasProperty("flgDiscountType")) {
                            //////// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("flgDiscountType").toString().isEmpty()) {
                                flgDiscountType = 0;
                            } else {
                                flgDiscountType = Integer.parseInt(tableRow.getProperty("flgDiscountType").toString().trim());

                            }
                        }
				/*String SchemeID="0";
				String SchemeName="0";
				String SchemeType="0";
				String StoreType="0";
				int CombTypeID=0;*/

                        dbengine.saveSOAPdataSchemeList(SchemeID, SchemeName, SchemeType, StoreType, CombTypeID, flgDiscountType);
                    }
                }
            }

            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetSchemeList :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker callWebServiceForGettingStoreInformation(Context ctx, String IMEI) {
        this.context = ctx;

        ////// System.out.println("jai called method in service worker");
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/fnGetOutLetInfoOnQuadVolumeCategoryBasis";
        final String METHOD_NAME = "fnGetOutLetInfoOnQuadVolumeCategoryBasis";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        ////// System.out.println("jai h1");

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);
        ////// System.out.println("jai h2");

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //ByVal IMEI As String, ByVal QuadId As Integer, ByVal VolumeSlabOptionNo As Integer, ByVal CategoryId As Integer)


            //IMEI="863580016068899";
            client.addProperty("IMEI", IMEI.toString());
			/*client.addProperty("QuadId", QuadId);
			client.addProperty("VolumeSlabOptionNo", VolumeSlabOptionNo);
			client.addProperty("CategoryId", CategoryId);*/
            ////// System.out.println("jai h3");

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();

            ////// System.out.println("jai responseBody one :"+responseBody);
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            ////// System.out.println("jai responseBody two :"+responseBody);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);


            ////// System.out.println("jai table :"+table);

            // #1

            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            ////// System.out.println("chkTblStoreListContainsRow: for Route "+ chkTblStoreListContainsRow);

            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 0) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);

                        String OutID = "NA";
                        String OutletName = "NA";
                        String OwnerName = "NA";
                        String ContactNo = "NA";
                        String MarketAreaName = "NA";
                        String Latitude = "NA";
                        String Longitutde = "NA";

				/*////// System.out.println("Ram OutID :"+tableRow.hasProperty("OutID"));
				////// System.out.println("Ram OutletName :"+tableRow.hasProperty("OutletName"));
				////// System.out.println("Ram OwnerName :"+tableRow.hasProperty("OwnerName"));
				////// System.out.println("Ram ContactNo :"+tableRow.hasProperty("ContactNo"));
				////// System.out.println("Ram MarketAreaName :"+tableRow.hasProperty("MarketAreaName"));
				////// System.out.println("Ram Latitude :"+tableRow.hasProperty("Latitude"));
				////// System.out.println("Ram Longitutde :"+tableRow.hasProperty("Longitutde"));*/


                        if ((tableRow.hasProperty("OutletName")) || (tableRow.hasProperty("OwnerName")) ||
                                (tableRow.hasProperty("ContactNo")) || (tableRow.hasProperty("MarketAreaName"))
                                || (tableRow.hasProperty("Latitude")) || (tableRow.hasProperty("Latitude"))) {

                            if ((!tableRow.hasProperty("OutID"))) {

                            } else {

                                OutID = tableRow.getProperty("OutID").toString().trim();
                            }
                            if ((!tableRow.hasProperty("OutletName"))) {

                            } else {

                                OutletName = tableRow.getProperty("OutletName").toString().trim();
                            }

                            if ((!tableRow.hasProperty("OwnerName"))) {

                            } else {
                                OwnerName = tableRow.getProperty("OwnerName").toString().trim();
                            }
                            if ((!tableRow.hasProperty("ContactNo"))) {

                            } else {
                                ContactNo = tableRow.getProperty("ContactNo").toString().trim();
                            }
                            if ((!tableRow.hasProperty("MarketAreaName"))) {

                            } else {
                                MarketAreaName = tableRow.getProperty("MarketAreaName").toString().trim();
                            }
                            if ((!tableRow.hasProperty("Latitude"))) {

                            } else {
                                Latitude = tableRow.getProperty("Latitude").toString().trim();
                            }
                            if ((!tableRow.hasProperty("Longitutde"))) {

                            } else {
                                Longitutde = tableRow.getProperty("Longitutde").toString().trim();
                            }


				/*////// System.out.println("Ram OutID Value befroe saving If :"+OutID);
				////// System.out.println("Ram OutletName Value befroe saving If :"+OutletName);
				////// System.out.println("Ram OwnerName Value befroe saving If :"+OwnerName);
				////// System.out.println("Ram ContactNo Value befroe saving If :"+ContactNo);
				////// System.out.println("Ram MarketAreaName Value befroe saving If :"+MarketAreaName);
				////// System.out.println("Ram Latitude Value befroe saving If :"+Latitude);
				////// System.out.println("Ram Longitutde Value befroe saving If :"+Longitutde);*/
                            dbengine.saveOutLetInfoOnQuadVolumeCategoryBasis(OutID, OutletName, OwnerName, ContactNo,
                                    MarketAreaName, Latitude, Longitutde);
                        } else {
					/*////// System.out.println("Ram OutID Value befroe saving else :"+OutID);
					////// System.out.println("Ram OutletName Value befroe saving else :"+OutletName);
					////// System.out.println("Ram OwnerName Value befroe saving else :"+OwnerName);
					////// System.out.println("Ram ContactNo Value befroe saving else :"+ContactNo);
					////// System.out.println("Ram MarketAreaName Value befroe saving else :"+MarketAreaName);
					////// System.out.println("Ram Latitude Value befroe saving else :"+Latitude);
					////// System.out.println("Ram Longitutde Value befroe saving else :"+Longitutde);*/

                            dbengine.saveOutLetInfoOnQuadVolumeCategoryBasis(OutID, OutletName, OwnerName, ContactNo,
                                    MarketAreaName, Latitude, Longitutde);
                        }

                    }
                }
            }


            // #4

            setmovie.director = "1";

            return setmovie;

        } catch (Exception e) {

            // System.out.println("Aman Exception occur in fnGetOutLetInfoOnQuadVolumeCategoryBasis :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getallSchemeDetails(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetSchemeDetails";
        final String METHOD_NAME = "GetSchemeDetails";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service

        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);

            client.addProperty("bydate", dateVAL.toString());
            /*client.addProperty("IMEINo", uuid.toString());*/

            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");

            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);

                        //////// System.out.println("exec DIS");
                        String dis = tableRow.getProperty("Discount").toString().trim();

                        //////// System.out.println("dis"+ dis);
                        Double valDIS = Double.parseDouble(dis);
                        //////// System.out.println("valDIS"+ valDIS);


                        String slabFrom = tableRow.getProperty("SlabFrom").toString().trim();
                        String slabTo = tableRow.getProperty("SlabTo").toString().trim();
                        String FreeQty = tableRow.getProperty("FreeQuantity").toString().trim();

                        String NewFreePrdID = "";

                        if (tableRow.getProperty("FreePrdID").toString().isEmpty()) {
                        } else {
                            NewFreePrdID = tableRow.getProperty("FreePrdID").toString().trim();
                        }


                        String FreeProductId = tableRow.getProperty("FreeQuantity").toString().trim();

                        int slabF = Integer.parseInt(slabFrom);
                        int slabT = Integer.parseInt(slabTo);
                        int freeQ = Integer.parseInt(FreeQty);

                        dbengine.saveSOAPdataSchemeDetails(tableRow.getProperty("SchemeDetID").toString().trim(), tableRow.getProperty("SchemeID").toString().trim(), slabF, slabT, freeQ, valDIS, NewFreePrdID);
                    }
                }
            }

            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetSchemeDetails :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }


    public ServiceWorker getCategory(Context ctx, String uuid) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetCategoryMstr";
        final String METHOD_NAME = "GetCategoryMstr";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);


        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //////// System.out.println("soap obj date: "+ dateVAL);


            //client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());


            //////// System.out.println("SRVC WRKR - dateVAL.toString(): "+dateVAL.toString());
            ////// System.out.println("SRVC WRKR - uuid.toString(): "+uuid.toString());
            ////// System.out.println("Arjun Calling category webservice 1");
            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();


            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }

            int chkTblCategoryListContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkTblCategoryListContainsRow = 1;
                dbengine.Delete_tblCategory_for_refreshData();
            }
            ////// System.out.println("chkTblStoreListContainsRow: for Category "+ chkTblStoreListContainsRow);

            if (chkTblCategoryListContainsRow == 1) {
                //////// System.out.println("h1");
                ////// System.out.println("Arjun Calling category webservice 4");
                //////// System.out.println("table.getPropertyCount()"+table.getPropertyCount());
                if (table.getPropertyCount() > 0) {
                    //////// System.out.println("h2");
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);


                        String stID = "NA";
                        String deDescr = "NA";
                        int CatOrdr = 0;

                        if ((tableRow.hasProperty("NODEID"))) {
                            if (tableRow.getProperty("NODEID").toString().isEmpty()) {
                                stID = "NA";
                            } else {
                                stID = tableRow.getProperty("NODEID").toString().trim();
                            }
                        }

                        if ((tableRow.hasProperty("CATEGORY"))) {
                            if (tableRow.getProperty("CATEGORY").toString().isEmpty()) {
                                deDescr = "NA";
                            } else {
                                deDescr = tableRow.getProperty("CATEGORY").toString().trim();
                            }
                        }


                        if (tableRow.hasProperty("CatOrdr")) {
                            if (tableRow.getProperty("CatOrdr").toString().isEmpty()) {
                                CatOrdr = 0;
                            } else {
                                CatOrdr = Integer.parseInt(tableRow.getProperty("CatOrdr").toString().trim());

                            }
                        }


                        dbengine.saveCategory(stID.trim(), deDescr.trim(), CatOrdr);


                    }
                }
            } //aa
            //dbengine.close();


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 3;
            // System.out.println("ServiceWorkerNitish getCategory Inside");
            return setmovie;

        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetCategoryMstr :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getCancelReasonsExecution(Context ctx, String fdate, String rId, String uuid) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetCancelReasonsExecution";
        final String METHOD_NAME = "GetCancelReasonsExecution";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);


        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //////// System.out.println("soap obj date: "+ dateVAL);


            //client.addProperty("bydate", dateVAL.toString());
            client.addProperty("bydate", fdate.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rId.toString());


            //////// System.out.println("SRVC WRKR - dateVAL.toString(): "+dateVAL.toString());
            ////// System.out.println("SRVC WRKR - uuid.toString(): "+uuid.toString());
            ////// System.out.println("Arjun Calling category webservice 1");
            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.bodyIn;


            int totalCount = responseBody.getPropertyCount();

            // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            //   dbengine.open();


            dbengine.delTblReasonOrderCncl();


            NodeList tblOrderCancelReasons = doc.getElementsByTagName("tblOrderCancelReasons");
            for (int i = 0; i < tblOrderCancelReasons.getLength(); i++) {

                String reasonCodeId = "0";

                String reasonDescr = "N/A";


                Element element = (Element) tblOrderCancelReasons.item(i);

                if (!element.getElementsByTagName("ReasonCodeID").equals(null)) {

                    NodeList ReasonCodeIDNode = element.getElementsByTagName("ReasonCodeID");
                    Element line = (Element) ReasonCodeIDNode.item(0);

                    if (ReasonCodeIDNode.getLength() > 0) {

                        reasonCodeId = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("ReasonDescr").equals(null)) {

                    NodeList ReasonDescrNode = element.getElementsByTagName("ReasonDescr");
                    Element line = (Element) ReasonDescrNode.item(0);

                    if (ReasonDescrNode.getLength() > 0) {

                        reasonDescr = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.insertReasonCanclOrder(reasonCodeId, reasonDescr);
            }


            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 3;
            // System.out.println("ServiceWorkerNitish getCategory Inside");
            return setmovie;

        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetCategoryMstr :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getalllastTransactionDetails(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetlastTransactionDetails";
        final String METHOD_NAME = "GetlastTransactionDetails";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);
        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

			/*////// System.out.println("soap obj date: "+ dateVAL);
			////// System.out.println("sunil dateVAL.toString() "+ dateVAL.toString());
			////// System.out.println("sunil uuid.toString(): "+ uuid.toString());
			////// System.out.println("sunilrID.toString(): "+ rID.toString());*/

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: sunil" + "dbengine.open");
            //////// System.out.println("sunil chkTblStoreListContainsRow sunil" +chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("sunil  count row: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("sunil value: "+ i);

				/*////// System.out.println("table - prop count: "+ table.getPropertyCount());
				tableRow = (SoapObject) table.getProperty(i);
				////// System.out.println("i value: "+ i);*/


                        String defPRODID = "NA";
                        String defLOD = "NA";
                        int defQTY = 0;
                        int pSampleQty = 0;
                        int CategoryID = 0;


                        if ((!tableRow.hasProperty("ProductID")) || (!tableRow.hasProperty("LastTransDate")) || (!tableRow.hasProperty("Quantity")) || (!tableRow.hasProperty("SampleQuantity"))) {
                            //(tableRow.getProperty("ProductID").toString().isEmpty()) || (tableRow.getProperty("LastTransDate").toString().isEmpty()) || (tableRow.getProperty("Quantity").toString().isEmpty())){
                            //////// System.out.println("sunil ProductID all");
                            if ((!tableRow.hasProperty("ProductID"))) {
                                //////// System.out.println("sunil ProductID not");
                            } else {
                                //////// System.out.println("sunil ProductID ");
                                defPRODID = tableRow.getProperty("ProductID").toString().trim();
                            }

                            if ((!tableRow.hasProperty("LastTransDate"))) {
                                //////// System.out.println("sunil LastTransDate not");
                            } else {
                                //////// System.out.println("sunil LastTransDate ");
                                defLOD = tableRow.getProperty("LastTransDate").toString().trim();
                            }

                            if ((!tableRow.hasProperty("Quantity"))) {
                                //////// System.out.println("sunil Quantity not");
                            } else {
                                //////// System.out.println("sunil Quantity ");
                                String lastQty = tableRow.getProperty("Quantity").toString().trim();

                                defQTY = Integer.parseInt(lastQty);
                            }
                            if ((!tableRow.hasProperty("SampleQuantity"))) {
                                //////// System.out.println("sunil SampleQuantity not");
                            } else {
                                //////// System.out.println("sunil SampleQuantity not");
                                String strpSampleQty = tableRow.getProperty("SampleQuantity").toString().trim();
                                pSampleQty = Integer.parseInt(strpSampleQty);
                            }
                            if (tableRow.hasProperty("CategoryID")) {
                                if (tableRow.getProperty("CategoryID").toString().isEmpty()) {
                                    CategoryID = 0;
                                } else {
                                    CategoryID = Integer.parseInt(tableRow.getProperty("CategoryID").toString().trim());
                                }
                            }
                            //////// System.out.println("sunil saveSOAPdataLastTransactionDetails" + tableRow.getProperty("StoreID").toString().trim() +" "+ defPRODID +" "+ defLOD +" "+ defQTY);
                            dbengine.saveSOAPdataLastTransactionDetails(tableRow.getProperty("StoreID").toString().trim(), defPRODID, defLOD, defQTY, pSampleQty, CategoryID);
                        } else {
                            String lastQty = tableRow.getProperty("Quantity").toString().trim();

                            int lastQ = Integer.parseInt(lastQty);

                            String strpSampleQty = tableRow.getProperty("SampleQuantity").toString().trim();
                            pSampleQty = Integer.parseInt(strpSampleQty);

                            //////// System.out.println("sunil else saveSOAPdataLastTransactionDetails(inside-else): "+ tableRow.getProperty("StoreID").toString().trim() +" "+ tableRow.getProperty("ProductID").toString().trim() +" "+ tableRow.getProperty("LastTransDate").toString().trim() +" "+lastQ);
                            dbengine.saveSOAPdataLastTransactionDetails(tableRow.getProperty("StoreID").toString().trim(), tableRow.getProperty("ProductID").toString().trim(), tableRow.getProperty("LastTransDate").toString().trim(), lastQ, pSampleQty, CategoryID);
                        }
                        // String lastQty =
                        // tableRow.getProperty("Quantity").toString().trim();

                        // int lastQ = Integer.parseInt(lastQty);

                        // dbengine.saveSOAPdataLastTransactionDetails(tableRow.getProperty("StoreID").toString().trim(),
                        // tableRow.getProperty("ProductID").toString().trim(),
                        // tableRow.getProperty("LastTransDate").toString().trim(),
                        // lastQ);
                    }
                }
            }


            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetlastTransactionDetails :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getallschemeStoreTypeMap(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetSchemeStoreTypeMap";
        final String METHOD_NAME = "GetSchemeStoreTypeMap";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);

            client.addProperty("bydate", dateVAL.toString());

            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");
            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);

                        String strSchemeID = tableRow.getProperty("SchemeID").toString().trim();

                        String strStoreTypeID = tableRow.getProperty("StoreTypeID").toString().trim();

                        int InStoreTypeID = Integer.parseInt(strStoreTypeID);

                        dbengine.saveSOAPdataSchemeSchemeStoreTypeMap(strSchemeID, InStoreTypeID);
                    }
                }
            }

            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetSchemeStoreTypeMap :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getallschemeProductMap(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetSchemeProductMap";
        final String METHOD_NAME = "GetSchemeProductMap";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("IMEINo", uuid.toString());


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");
            //////// System.out.println(dbengine);
            //////// System.out.println(cw);

            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);

                        String NewFreePrdID = null;

				/*if ((tableRow.getProperty("FreePrdID").toString().isEmpty())) {

					if (tableRow.getProperty("FreePrdID").toString().isEmpty()) {
					}
					else
					{
						NewFreePrdID = tableRow.getProperty("FreePrdID").toString().trim();
					}

				}
				else
				{
					NewFreePrdID = tableRow.getProperty("FreePrdID").toString().trim();

				}*/

                        String strSchemeID = tableRow.getProperty("SchemeID").toString().trim();

                        //int InSchemeID = Integer.parseInt(strSchemeID);
                        String strProductID = tableRow.getProperty("ProductID").toString().trim();

                        String strSchemeType = tableRow.getProperty("SchemeType").toString().trim();

                        dbengine.saveSOAPdataschemeProductMap(strSchemeID, strProductID, Integer.parseInt(strSchemeType));//NewFreePrdID
                    }
                }
            }

            dbengine.close();        // #4

			/*setmovie.director = tableRow.getProperty("Director").toString();
			setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetSchemeProductMap :"+e.toString());


            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getallPDALastTranDateForSecondPage(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        final String SOAP_ACTION = "http://tempuri.org/GetPDALastTranDetForSecondPage";
        final String METHOD_NAME = "GetPDALastTranDetForSecondPage";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service

        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);
            ////// System.out.println("H1");

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);
            ////// System.out.println("H221");

            // get information XMl of tables that is returned

            table = (SoapObject) responseBody.getProperty(0);
            //////// System.out.println("H222");
            // #1

            //////// System.out.println("Debug: " + "dbengine.open");

            //chkTblStoreListContainsRow
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
                //////// System.out.println("H4");
            }
            //////// System.out.println("chkTblStoreListContainsRow: "+ chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    ////// System.out.println("H5");
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


					/*////// System.out.println("SOAP_STORE ID: " + tableRow.getProperty("StoreID").toString());
					////// System.out.println("SOAP_2_STORE ID: " + tableRow.getProperty("StoreID"));

					String slat = tableRow.getProperty("StoreLatitude").toString().trim();
					////// System.out.println("slat"+ slat);
					Double storeLat = Double.parseDouble(slat);

					////// System.out.println("storeLat"+ storeLat);

					String slon = tableRow.getProperty("StoreLongitude").toString().trim();
					Double storeLon = Double.parseDouble(slon);*/
                    String ssId = "NA";
                    String LTD = "NA";
                    String Rname = "NA";
                    String skuName = "NA";
                    String stock = "0";
                    String order = "0";
                    String free = "0";
                    ////// System.out.println("H6");

                    if ((!tableRow.hasProperty("StoreID")) || (!tableRow.hasProperty("LastTransDate")) || (!tableRow.hasProperty("RetailerName")) || (!tableRow.hasProperty("SKUName")) || (!tableRow.hasProperty("Stock")) || (!tableRow.hasProperty("OrderQty")) || (!tableRow.hasProperty("FreeQty"))) {
                        //(tableRow.getProperty("ProductID").toString().isEmpty()) || (tableRow.getProperty("LastTransDate").toString().isEmpty()) || (tableRow.getProperty("Quantity").toString().isEmpty())){
                        //////// System.out.println("H7");
                        if ((!tableRow.hasProperty("StoreID"))) {
                        } else {
                            ssId = tableRow.getProperty("StoreID").toString().trim();
                            //////// System.out.println("H8");
                        }

                        if ((!tableRow.hasProperty("LastTransDate"))) {
                        } else {
                            LTD = tableRow.getProperty("LastTransDate").toString().trim();
                            //////// System.out.println("H9");
                        }

                        if ((!tableRow.hasProperty("RetailerName"))) {
                        } else {
                            Rname = tableRow.getProperty("RetailerName").toString().trim();
                            //////// System.out.println("H10");
                        }
                        if ((!tableRow.hasProperty("SKUName"))) {
                        } else {
                            skuName = tableRow.getProperty("SKUName").toString().trim();
                            //////// System.out.println("H11");
                        }
                        if ((!tableRow.hasProperty("Stock"))) {
                        } else {
                            stock = tableRow.getProperty("Stock").toString().trim();
                            //////// System.out.println("H12");
                        }
                        if ((!tableRow.hasProperty("OrderQty"))) {
                        } else {
                            order = tableRow.getProperty("OrderQty").toString().trim();
                            //////// System.out.println("H13");
                        }
                        if ((!tableRow.hasProperty("FreeQty"))) {
                        } else {
                            free = tableRow.getProperty("FreeQty").toString().trim();
                            //////// System.out.println("H14");
                        }

                        //////// System.out.println("saveSOAPdataLastTransactionDetails" + tableRow.getProperty("StoreID").toString().trim() +" "+ defPRODID +" "+ defLOD +" "+ defQTY);
                        dbengine.saveSOAPdataPdaLastTranDateForSecondPage(ssId, LTD, Rname, skuName, stock, order, free);
                        //////// System.out.println("Data in If Case: " + ssId + " _ " +  LTD + " _ "+  Rname + " _ "+  skuName + " _ "+  stock + " _ "+ order + " _ "+ free);
                    } else {
                        //////// System.out.println("saveSOAPdataLastTransactionDetails(inside-else): "+ tableRow.getProperty("StoreID").toString().trim() +" "+ tableRow.getProperty("ProductID").toString().trim() +" "+ tableRow.getProperty("LastTransDate").toString().trim() +" "+lastQ);
                        dbengine.saveSOAPdataPdaLastTranDateForSecondPage(tableRow.getProperty("StoreID").toString().trim(), tableRow.getProperty("LastTransDate").toString().trim(), tableRow.getProperty("RetailerName").toString().trim(), tableRow.getProperty("SKUName").toString().trim(), tableRow.getProperty("Stock").toString().trim(), tableRow.getProperty("OrderQty").toString().trim(), tableRow.getProperty("FreeQty").toString().trim());
                        //////// System.out.println("Data in Else Case: " + tableRow.getProperty("StoreID").toString().trim() + " _ " +  tableRow.getProperty("LastTransDate").toString().trim() + " _ "+  tableRow.getProperty("RetailerName").toString().trim() + " _ "+  tableRow.getProperty("SKUName").toString().trim() + " _ "+  tableRow.getProperty("Stock").toString().trim() + " _ "+ tableRow.getProperty("OrderQty").toString().trim() + " _ "+ tableRow.getProperty("FreeQty").toString().trim());
                    }

                    //

                }
            }

            dbengine.close();        // #4

			/*setmovie.director = tableRow.getProperty("Director").toString();
			setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetPDALastTranDetForSecondPage :"+e.toString());

            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getallPDASchAppListForSecondPage(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetForPDASchemeApplicableList";
        final String METHOD_NAME = "GetForPDASchemeApplicableList";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service

        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);
            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            responseBody = (SoapObject) sse.getResponse();

            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }


            //// System.out.println("kamal Testing  3 :" + table.getAttribute("size").toString());

            int chkTblSchAppListContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkTblSchAppListContainsRow = 1;
                dbengine.Delete_tblspForPDASchemeApplicableList_for_refreshData();
            }
            //////// System.out.println("chkTblStoreListContainsRow: "+ chkTblStoreListContainsRow);
            if (chkTblSchAppListContainsRow == 1) {
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


					/*////// System.out.println("SOAP_STORE ID: " + tableRow.getProperty("StoreID").toString());
					////// System.out.println("SOAP_2_STORE ID: " + tableRow.getProperty("StoreID"));

					String slat = tableRow.getProperty("StoreLatitude").toString().trim();
					////// System.out.println("slat"+ slat);
					Double storeLat = Double.parseDouble(slat);

					////// System.out.println("storeLat"+ storeLat);

					String slon = tableRow.getProperty("StoreLongitude").toString().trim();
					Double storeLon = Double.parseDouble(slon);*/
                    String schID = "NA";
                    String sTYP = "NA";
                    String schDES = "NA";
                    int flgSpecialScheme = 0;


                    if ((!tableRow.hasProperty("SchemeID")) || (!tableRow.hasProperty("StoreType")) || (!tableRow.hasProperty("SchemeDesc"))) {
                        //(tableRow.getProperty("ProductID").toString().isEmpty()) || (tableRow.getProperty("LastTransDate").toString().isEmpty()) || (tableRow.getProperty("Quantity").toString().isEmpty())){

                        if ((!tableRow.hasProperty("SchemeID"))) {
                        } else {
                            schID = tableRow.getProperty("SchemeID").toString().trim();
                        }

                        if ((!tableRow.hasProperty("StoreType"))) {
                        } else {
                            sTYP = tableRow.getProperty("StoreType").toString().trim();
                        }

                        if ((!tableRow.hasProperty("SchemeDesc"))) {
                        } else {
                            schDES = tableRow.getProperty("SchemeDesc").toString().trim();
                        }
                        if ((!tableRow.hasProperty("flgSpecialScheme"))) {
                            flgSpecialScheme = 0;
                        } else {
                            flgSpecialScheme = Integer.parseInt(tableRow.getProperty("flgSpecialScheme").toString().trim());
                        }


                        //////// System.out.println("saveSOAPdataLastTransactionDetails" + tableRow.getProperty("StoreID").toString().trim() +" "+ defPRODID +" "+ defLOD +" "+ defQTY);
                        dbengine.saveSOAPdataPdaSchAppListForSecondPage(schID, sTYP, schDES, flgSpecialScheme);
                    } else {
                        //////// System.out.println("saveSOAPdataLastTransactionDetails(inside-else): "+ tableRow.getProperty("StoreID").toString().trim() +" "+ tableRow.getProperty("ProductID").toString().trim() +" "+ tableRow.getProperty("LastTransDate").toString().trim() +" "+lastQ);
                        dbengine.saveSOAPdataPdaSchAppListForSecondPage(tableRow.getProperty("SchemeID").toString().trim(), tableRow.getProperty("StoreType").toString().trim(), tableRow.getProperty("SchemeDesc").toString().trim(), Integer.parseInt(tableRow.getProperty("flgSpecialScheme").toString().trim()));
                    }

                    //

                }
            }

            dbengine.close();        // #4

			/*setmovie.director = tableRow.getProperty("Director").toString();
			setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 5;
            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetForPDASchemeApplicableList :"+e.toString());
            setmovie.director = e.toString();
            flagExecutedServiceSuccesfully = 0;
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getallStoreTypeDetails(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetSoreType";
        final String METHOD_NAME = "GetSoreType";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);
        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //////// System.out.println("soap obj date: "+ dateVAL);
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");

            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 0) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);


                        String stID = "NA";
                        String deDescr = "NA";


                        if ((!tableRow.hasProperty("ID")) || (!tableRow.hasProperty("Descr"))) {
                            //(tableRow.getProperty("ProductID").toString().isEmpty()) || (tableRow.getProperty("LastTransDate").toString().isEmpty()) || (tableRow.getProperty("Quantity").toString().isEmpty())){

                            if ((!tableRow.hasProperty("ID"))) {
                            } else {
                                stID = tableRow.getProperty("ID").toString().trim();
                            }

                            if ((!tableRow.hasProperty("Descr"))) {
                            } else {
                                deDescr = tableRow.getProperty("Descr").toString().trim();
                            }


                            dbengine.saveStoreTypeDetails(stID, deDescr);
                        } else {

                            dbengine.saveStoreTypeDetails(tableRow.getProperty("ID").toString().trim(), tableRow.getProperty("Descr").toString().trim());
                        }
                        // String lastQty =
                        // tableRow.getProperty("Quantity").toString().trim();

                        // int lastQ = Integer.parseInt(lastQty);

                        // dbengine.saveSOAPdataLastTransactionDetails(tableRow.getProperty("StoreID").toString().trim(),
                        // tableRow.getProperty("ProductID").toString().trim(),
                        // tableRow.getProperty("LastTransDate").toString().trim(),
                        // lastQ);
                    }
                }
            }


            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetSoreType :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }
	/*public ServiceWorker getallLocationDetails(Context ctx, String dateVAL, String uuid, String rID) {
		this.context = ctx;

		DBAdapter dbengine = new DBAdapter(context);
		dbengine.open();

		final String SOAP_ACTION = "http://tempuri.org/GetLocation";
		final String METHOD_NAME = "GetLocation";
		final String NAMESPACE = "http://tempuri.org/";
		final String URL = UrlForWebService;

		SoapObject table = null; // Contains table of dataset that returned
									// through SoapObject
		SoapObject client = null; // Its the client petition to the web service
		SoapObject tableRow = null; // Contains row of table
		SoapObject responseBody = null; // Contains XML content of dataset

		//SoapObject param
		HttpTransportSE transport = null; // That call webservice
		SoapSerializationEnvelope sse = null;

		sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
		// Note if class name isn't "ABC_CLASS_NAME" ,you must change
		sse.dotNet = true; // if WebService written .Net is result=true
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				URL,timeout);
		ServiceWorker setmovie = new ServiceWorker();
		try {
			client = new SoapObject(NAMESPACE, METHOD_NAME);

			//////// System.out.println("soap obj date: "+ dateVAL);


			client.addProperty("bydate", dateVAL.toString());
			client.addProperty("IMEINo", uuid.toString());
			client.addProperty("rID", rID.toString());


			sse.setOutputSoapObject(client);
			sse.bodyOut = client;
			androidHttpTransport.call(SOAP_ACTION, sse);

			// This step: get file XML
			responseBody = (SoapObject) sse.getResponse();
			// remove information XML,only retrieved results that returned
			responseBody = (SoapObject) responseBody.getProperty(1);

			// get information XMl of tables that is returned
			table = (SoapObject) responseBody.getProperty(0);

					// #1

			//dbengine.reCreateDB();
			//////// System.out.println("Debug: " + "dbengine.open");

			if(chkTblStoreListContainsRow==1)
			{
				if( table.getPropertyCount()>0)
				{
				for(int i = 0; i <= table.getPropertyCount() -1 ; i++){

				//////// System.out.println("table - prop count: "+ table.getPropertyCount());
				tableRow = (SoapObject) table.getProperty(i);
				//////// System.out.println("i value: "+ i);

				//////// System.out.println("table - prop count: "+ table.getPropertyCount());
				tableRow = (SoapObject) table.getProperty(i);
				//////// System.out.println("i value: "+ i);


				String stID = "NA";
				String deDescr = "NA";


				if((!tableRow.hasProperty("ID")) || (!tableRow.hasProperty("Descr")))
			{
				//(tableRow.getProperty("ProductID").toString().isEmpty()) || (tableRow.getProperty("LastTransDate").toString().isEmpty()) || (tableRow.getProperty("Quantity").toString().isEmpty())){

				if((!tableRow.hasProperty("ID")))
				{}
				else {
					stID = tableRow.getProperty("ID").toString().trim();
				}

				if((!tableRow.hasProperty("Descr")))
				{}
				else{
					deDescr = tableRow.getProperty("Descr").toString().trim();
				}

				//////// System.out.println("City Name : "+ deDescr);
				//////// System.out.println("City Id : "+ stID);
				dbengine.saveLocationDetails(stID.trim(), deDescr);
			}
				else{
					//////// System.out.println("City Name : "+ tableRow.getProperty("Descr").toString().trim());
					//////// System.out.println("City Id : "+ tableRow.getProperty("ID").toString().trim());
					dbengine.saveLocationDetails(tableRow.getProperty("ID").toString().trim(), tableRow.getProperty("Descr").toString().trim());
				}
						// String lastQty =
						// tableRow.getProperty("Quantity").toString().trim();

						// int lastQ = Integer.parseInt(lastQty);

						// dbengine.saveSOAPdataLastTransactionDetails(tableRow.getProperty("StoreID").toString().trim(),
						// tableRow.getProperty("ProductID").toString().trim(),
						// tableRow.getProperty("LastTransDate").toString().trim(),
						// lastQ);
					}
			}
			}


			dbengine.close();		// #4

			setmovie.director = "1";

			return setmovie;
//return counts;
		} catch (Exception e) {

			// System.out.println("Aman Exception occur in GetLocation :"+e.toString());
			setmovie.director = e.toString();
			setmovie.movie_name = e.toString();
			dbengine.close();
			return setmovie;
		}

	}
	public ServiceWorker getallAreaDetails(Context ctx, String dateVAL, String uuid, String rID) {
		this.context = ctx;

		DBAdapter dbengine = new DBAdapter(context);
		dbengine.open();

		final String SOAP_ACTION = "http://tempuri.org/GetArea";
		final String METHOD_NAME = "GetArea";
		final String NAMESPACE = "http://tempuri.org/";
		final String URL = UrlForWebService;

		SoapObject table = null; // Contains table of dataset that returned
									// through SoapObject
		SoapObject client = null; // Its the client petition to the web service
		SoapObject tableRow = null; // Contains row of table
		SoapObject responseBody = null; // Contains XML content of dataset

		//SoapObject param
		HttpTransportSE transport = null; // That call webservice
		SoapSerializationEnvelope sse = null;

		sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
		// Note if class name isn't "ABC_CLASS_NAME" ,you must change
		sse.dotNet = true; // if WebService written .Net is result=true
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				URL,timeout);

		ServiceWorker setmovie = new ServiceWorker();
		try {
			client = new SoapObject(NAMESPACE, METHOD_NAME);

			//////// System.out.println("soap obj date: "+ dateVAL);
			client.addProperty("IMEINo", uuid.toString());
			client.addProperty("rID", rID.toString());


			sse.setOutputSoapObject(client);
			sse.bodyOut = client;
			androidHttpTransport.call(SOAP_ACTION, sse);

			// This step: get file XML
			responseBody = (SoapObject) sse.getResponse();
			// remove information XML,only retrieved results that returned
			responseBody = (SoapObject) responseBody.getProperty(1);

			// get information XMl of tables that is returned
			table = (SoapObject) responseBody.getProperty(0);

					// #1

			//dbengine.reCreateDB();
			//////// System.out.println("Debug: " + "dbengine.open");

			if(chkTblStoreListContainsRow==1)
			{
				if( table.getPropertyCount()>0)
				{
				for(int i = 0; i <= table.getPropertyCount() -1 ; i++){

				//////// System.out.println("table - prop count: "+ table.getPropertyCount());
				tableRow = (SoapObject) table.getProperty(i);
				//////// System.out.println("i value: "+ i);

				//////// System.out.println("table - prop count: "+ table.getPropertyCount());
				tableRow = (SoapObject) table.getProperty(i);
				//////// System.out.println("i value: "+ i);


				String stID = "NA";
				String deDescr = "NA";
				String deLocationID = "NA";

				//LocationID
				if((!tableRow.hasProperty("ID")) || (!tableRow.hasProperty("Descr")) || (!tableRow.hasProperty("LocationID")))
			{
				//(tableRow.getProperty("ProductID").toString().isEmpty()) || (tableRow.getProperty("LastTransDate").toString().isEmpty()) || (tableRow.getProperty("Quantity").toString().isEmpty())){

				if((!tableRow.hasProperty("ID")))
				{}
				else {
					stID = tableRow.getProperty("ID").toString().trim();
				}

				if((!tableRow.hasProperty("Descr")))
				{}
				else{
					deDescr = tableRow.getProperty("Descr").toString().trim();
				}
				if((!tableRow.hasProperty("LocationID")))
				{}
				else{
					deLocationID = tableRow.getProperty("LocationID").toString().trim();
				}

				dbengine.saveAreaDetails(stID, deDescr, deLocationID);
			}
				else{

				dbengine.saveAreaDetails(tableRow.getProperty("ID").toString().trim(), tableRow.getProperty("Descr").toString().trim(), tableRow.getProperty("LocationID").toString().trim());
				}
						// String lastQty =
						// tableRow.getProperty("Quantity").toString().trim();

						// int lastQ = Integer.parseInt(lastQty);

						// dbengine.saveSOAPdataLastTransactionDetails(tableRow.getProperty("StoreID").toString().trim(),
						// tableRow.getProperty("ProductID").toString().trim(),
						// tableRow.getProperty("LastTransDate").toString().trim(),
						// lastQ);
					}
			}
			}


			dbengine.close();		// #4

			setmovie.director = "1";

			return setmovie;
//return counts;
		} catch (Exception e) {

			// System.out.println("Aman Exception occur in GetArea :"+e.toString());
			setmovie.director = e.toString();
			setmovie.movie_name = e.toString();
			dbengine.close();
			return setmovie;
		}

	}
	*/


    public ServiceWorker getallPDALastInvoiceDet(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetPDALastInvoiceDet";
        final String METHOD_NAME = "GetPDALastInvoiceDet";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset
        decimalFormat.applyPattern(pattern);
        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //////// System.out.println("soap obj date: "+ dateVAL);

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);


            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");

            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 0) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);


                        String invID = "0";
                        String LTDate = "NA";
                        String BalAmt = "0.00";
                        Double CreditPreviousDue = 0.00;

                        // || (tableRow.hasProperty("LastTransDate")) || (tableRow.hasProperty("BalanceAmount"))
                        //LocationID
                        if ((tableRow.hasProperty("InvoiceID"))) {
                            //(tableRow.getProperty("ProductID").toString().isEmpty()) || (tableRow.getProperty("LastTransDate").toString().isEmpty()) || (tableRow.getProperty("Quantity").toString().isEmpty())){

                            invID = tableRow.getProperty("InvoiceID").toString().trim();
                        }
                        if ((tableRow.hasProperty("LastTransDate"))) {
                            //(tableRow.getProperty("ProductID").toString().isEmpty()) || (tableRow.getProperty("LastTransDate").toString().isEmpty()) || (tableRow.getProperty("Quantity").toString().isEmpty())){

                            LTDate = tableRow.getProperty("LastTransDate").toString().trim();
                        }
                        if ((tableRow.hasProperty("BalanceAmount"))) {
                            //(tableRow.getProperty("ProductID").toString().isEmpty()) || (tableRow.getProperty("LastTransDate").toString().isEmpty()) || (tableRow.getProperty("Quantity").toString().isEmpty())){

                            BalAmt = tableRow.getProperty("BalanceAmount").toString().trim();
                        }
                        if ((tableRow.hasProperty("CreditPreviousDue"))) {
                            //(tableRow.getProperty("ProductID").toString().isEmpty()) || (tableRow.getProperty("LastTransDate").toString().isEmpty()) || (tableRow.getProperty("Quantity").toString().isEmpty())){

                            CreditPreviousDue = Double.parseDouble(decimalFormat.format(tableRow.getProperty("CreditPreviousDue").toString().trim()));
                            //Double.parseDouble(tableRow.getProperty("CreditPreviousDue").toString().trim());
                        }
                        //////// System.out.println("PDA LAst Invoice Deails - " + (i +1) + ":"  + (tableRow.getProperty("StoreID").toString().trim()+ " ," + invID + "," + LTDate +" , "  + BalAmt + ", " + CreditPreviousDue ));
                        dbengine.SavePDALastInvoiceDet(tableRow.getProperty("StoreID").toString().trim(),
                                invID, LTDate, BalAmt, CreditPreviousDue);

                        // String lastQty =
                        // tableRow.getProperty("Quantity").toString().trim();

                        // int lastQ = Integer.parseInt(lastQty);

                        // dbengine.saveSOAPdataLastTransactionDetails(tableRow.getProperty("StoreID").toString().trim(),
                        // tableRow.getProperty("ProductID").toString().trim(),
                        // tableRow.getProperty("LastTransDate").toString().trim(),
                        // lastQ);
                    }
                }
            }


            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {

            dbengine.close();

            // System.out.println("Aman Exception occur in GetPDALastInvoiceDet :"+e.toString());
            //////// System.out.println("aman getallProduct: 13 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getallPDATargetQtyForSecondPage(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetPDATargetQtyForSecondPage";
        final String METHOD_NAME = "GetPDATargetQtyForSecondPage";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //////// System.out.println("soap obj date: "+ dateVAL);


            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");

            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() > 0) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);


                        String SKUShortName = "NA";
                        String Target = "0";
                        String PrdIDNew = "NA";
                        if ((tableRow.hasProperty("SKUShortName"))) {
                            SKUShortName = tableRow.getProperty("SKUShortName").toString().trim();
                        } else {
                            SKUShortName = "NA";
                        }

                        if ((tableRow.hasProperty("TargetQty"))) {
                            Target = tableRow.getProperty("TargetQty").toString().trim();
                        } else {
                            Target = "0";
                        }

                        if ((tableRow.hasProperty("ProductID"))) {
                            PrdIDNew = tableRow.getProperty("ProductID").toString().trim();
                        } else {
                            PrdIDNew = "NA";
                        }

                        dbengine.SavePDATargetQtyForSecondPage(tableRow.getProperty("StoreID").toString().trim(), PrdIDNew, SKUShortName, Target);
                        // String lastQty =
                        // tableRow.getProperty("Quantity").toString().trim();

                        // int lastQ = Integer.parseInt(lastQty);

                        // dbengine.saveSOAPdataLastTransactionDetails(tableRow.getProperty("StoreID").toString().trim(),
                        // tableRow.getProperty("ProductID").toString().trim(),
                        // tableRow.getProperty("LastTransDate").toString().trim(),
                        // lastQ);
                    }
                }
            }


            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetPDATargetQtyForSecondPage :"+e.toString());
            //////// System.out.println("aman getallProduct: 14 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getallPDAtblSyncSummuryDetails(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetSyncSummuryDetails";
        final String METHOD_NAME = "GetSyncSummuryDetails";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);

            //////// System.out.println("soap obj IMEINo: "+ uuid);

            //////// System.out.println("soap obj rID: "+ rID);

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

		/*	////// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
			////// System.out.println("Abhinav Raj 121 bydate :"+ dateVAL.toString());
			////// System.out.println("Abhinav Raj 121 rID :"+ rID.toString());*/


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");

            //////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {
                String ActualCalls = "0";
                String ProductiveCalls = "0";
                String TotSalesValue = "0";
                String TotKGSales = "0.0";
                String TotFreeQtyKGSales = "0.0";
                String TotSampleKGSales = "0.0";
                String TotLTSales = "0.0";
                String TotFreeQtyLTSales = "0.0";
                String TotSampleLTSales = "0.0";
                String TotDiscountKGSales = "0.0";
                String TotDiscountLTales = "0.0";
                int Lines = 0;
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);

                    ActualCalls = tableRow.getProperty("ActualCalls").toString().trim();

                    ProductiveCalls = tableRow.getProperty("ProdCalls").toString().trim();

                    TotSalesValue = tableRow.getProperty("SalesVal").toString().trim();

                    TotKGSales = tableRow.getProperty("TotalKgs").toString().trim();

                    TotFreeQtyKGSales = tableRow.getProperty("FreeKgs").toString().trim();

                    TotSampleKGSales = tableRow.getProperty("SampleKgs").toString().trim();

                    TotLTSales = tableRow.getProperty("TotalOrderLt").toString().trim();

                    TotFreeQtyLTSales = tableRow.getProperty("TotalFreeLt").toString().trim();

                    TotSampleLTSales = tableRow.getProperty("TotalSamplerLt").toString().trim();

                    TotDiscountKGSales = tableRow.getProperty("DisValKgs").toString().trim();

                    TotDiscountLTales = tableRow.getProperty("DisValLt").toString().trim();

                    Lines = Integer.parseInt(tableRow.getProperty("Lines").toString().trim());

					/*	////// System.out.println("Abhinav Raj 121 ActualCalls: " + ActualCalls);
						////// System.out.println("Abhinav Raj 121 ProductiveCalls: " + ProductiveCalls);
						////// System.out.println("Abhinav Raj 121 TotSalesValue: " + TotSalesValue);
						////// System.out.println("Abhinav Raj 121 TotKGSales: " + TotKGSales);
						////// System.out.println("Abhinav Raj 121 TotFreeQtyKGSales: " + TotFreeQtyKGSales);
						////// System.out.println("Abhinav Raj 121 TotSampleKGSales: " + TotSampleKGSales);
						//////// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
*/
                    dbengine.SavetblSyncSummuryDetails(ActualCalls, ProductiveCalls, TotSalesValue, TotKGSales, TotFreeQtyKGSales, TotSampleKGSales, TotLTSales, TotFreeQtyLTSales, TotSampleLTSales, TotDiscountKGSales, TotDiscountLTales, Lines);

                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 22;
            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetSyncSummuryDetails :"+e.toString());
            //////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker GetPDAIsSchemeApplicable(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetPDAIsSchemeApplicable";
        final String METHOD_NAME = "GetPDAIsSchemeApplicable";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service

        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);
            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }

            client.addProperty("bydate", dateVAL.toString());

            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();


            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);

            } else {

            }

            int chkTblSchemeApplicableContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkTblSchemeApplicableContainsRow = 1;
            }

            int IsSchemeApplicable = 3;
            if (chkTblSchemeApplicableContainsRow == 1) {

                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);
                    String NewStoreType = "0";


                    if (tableRow.hasProperty("IsSchemeApplicable")) {
                        if ((tableRow.getProperty("IsSchemeApplicable").toString().isEmpty())) {

                        } else {
                            IsSchemeApplicable = Integer.parseInt(tableRow.getProperty("IsSchemeApplicable").toString().trim());
                        }
                    }


                }
            }
            dbengine.SavePDAIsSchemeApplicable(IsSchemeApplicable);

            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 21;
            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetPDAIsSchemeApplicable :"+e.toString());
            dbengine.close();
            //////// System.out.println("aman getallProduct: 17 "+e.toString());
            setmovie.director = e.toString();
            flagExecutedServiceSuccesfully = 0;
            setmovie.movie_name = e.toString();
            return setmovie;
        }

    }

    public ServiceWorker getallPDAtblSyncSummuryForProductDetails(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);

        final String SOAP_ACTION = "http://tempuri.org/GetSyncSummuryForProductDetailsNew";
        final String METHOD_NAME = "GetSyncSummuryForProductDetailsNew";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

			/*////// System.out.println("GetSyncSummuryForProductDetails date: "+ dateVAL.toString());

			////// System.out.println("GetSyncSummuryForProductDetails IMEINo: "+ uuid);

			////// System.out.println("GetSyncSummuryForProductDetails rID: "+ rID);
			*/
            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");

            //////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {
                //dbengine.open();
				/*String ActualCalls="0";
				String ProductiveCalls="0";
				String TotSalesValue ="0";
				String TotKGSales ="0.0";
				String TotFreeQtyKGSales ="0.0";
				String TotSampleKGSales ="0.0";
				String TotLTSales="0.0";
				String TotFreeQtyLTSales="0.0";
				String TotSampleLTSales="0.0";*/

                String SkuName = "0";
                String OrderQty = "0";
                String FreeQty = "0";
                String SampleQty = "0.0";
                String TotalOrderKgs = "0.0";
                String TotalFreeKgs = "0.0";
                String TotalSampleKgs = "0.0";
                String TotalSales = "0.0";

                int Lines = 0;
				/*String TotFreeQtyLTSales="0.0";
				String TotSampleLTSales="0.0";*/

                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);

                    SkuName = tableRow.getProperty("SKUShortName").toString().trim();

                    OrderQty = tableRow.getProperty("Sale").toString().trim();

                    FreeQty = tableRow.getProperty("Free").toString().trim();

                    SampleQty = tableRow.getProperty("Sample").toString().trim();

                    TotalOrderKgs = tableRow.getProperty("TotalOrderKgs").toString().trim();

                    TotalFreeKgs = tableRow.getProperty("TotalFreeKgs").toString().trim();

                    TotalSampleKgs = tableRow.getProperty("TotalSampleKgs").toString().trim();

                    TotalSales = tableRow.getProperty("TotalSales").toString().trim();

                    Lines = Integer.parseInt(tableRow.getProperty("Lines").toString().trim());

                    // TotSampleLTSales = tableRow.getProperty("TotalSamplerLt").toString().trim();

						/*////// System.out.println("SkuName: in service worker " + SkuName);
						////// System.out.println("OrderQty: " + OrderQty);
						////// System.out.println("FreeQty: " + FreeQty);
						////// System.out.println("SampleQty: " + SampleQty);
						////// System.out.println("TotalOrderKgs: " + TotalOrderKgs);
						////// System.out.println("TotalFreeKgs: " + TotalFreeKgs);
						////// System.out.println("TotalSampleKgs: " + TotalSampleKgs);
						////// System.out.println("TotalSales: " + TotalSales);
					*/

                    //dbengine.SavetblSyncSummuryDetails(ActualCalls,ProductiveCalls,TotSalesValue,TotKGSales,TotFreeQtyKGSales,TotSampleKGSales,TotLTSales,TotFreeQtyLTSales,TotSampleLTSales);
                    dbengine.open();        // #4
                    dbengine.SavetblSyncSummuryForProductDetails(SkuName, OrderQty, FreeQty, SampleQty, TotalOrderKgs, TotalFreeKgs, TotalSampleKgs, TotalSales, Lines);
                    dbengine.close();        // #4
                }


            }


            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetSyncSummuryForProductDetails :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            return setmovie;
        }

    }

    /*public ServiceWorker getallPDAtblSyncSummuryForEachStoreWiseDetails(Context ctx, String dateVAL, String uuid, String rID) {
		this.context = ctx;

		DBAdapter dbengine = new DBAdapter(context);
		dbengine.open();
		final String SOAP_ACTION = "http://tempuri.org/GetSyncSummuryForEachStoreWiseDetails";
		final String METHOD_NAME = "GetSyncSummuryForEachStoreWiseDetails";
		final String NAMESPACE = "http://tempuri.org/";
		final String URL = UrlForWebService;


		SoapObject table = null; // Contains table of dataset that returned
									// through SoapObject
		SoapObject client = null; // Its the client petition to the web service
		SoapObject tableRow = null; // Contains row of table
		SoapObject responseBody = null; // Contains XML content of dataset

		//SoapObject param
		HttpTransportSE transport = null; // That call webservice
		SoapSerializationEnvelope sse = null;



		sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
		// Note if class name isn't "movie" ,you must change
		sse.dotNet = true; // if WebService written .Net is result=true
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				URL,timeout);

		ServiceWorker setmovie = new ServiceWorker();
		try {
			client = new SoapObject(NAMESPACE, METHOD_NAME);

			//String dateVAL = "00.00.0000";

			////// System.out.println("soap obj date: "+ dateVAL);

			////// System.out.println("soap obj IMEINo: "+ uuid);

			////// System.out.println("soap obj rID: "+ rID);

			client.addProperty("bydate", dateVAL.toString());
			client.addProperty("IMEINo", uuid.toString());
			client.addProperty("rID", rID.toString());


			client.addProperty("IMEINo", uuid.toString());

			sse.setOutputSoapObject(client);
			sse.bodyOut = client;
			androidHttpTransport.call(SOAP_ACTION, sse);

			// This step: get file XML
			responseBody = (SoapObject) sse.getResponse();
			// remove information XML,only retrieved results that returned
			responseBody = (SoapObject) responseBody.getProperty(1);

			// get information XMl of tables that is returned
			table = (SoapObject) responseBody.getProperty(0);

					// #1

			//dbengine.reCreateDB();
			//////// System.out.println("Debug: " + "dbengine.open");

			//////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
			//dbengine.reCreateDB();
			if(chkTblStoreListContainsRow==1)
			{

				String RetailerName="0";
				String Sales200g="0";
				String Free200g ="0";
				String Sample200g ="0.0";
				String Sales400g="0";
				String Free400g ="0";
				String Sample400g ="0.0";
				String OrderQtyTotal ="0.0";
				String TotalKgs ="0.0";


					for(int i = 0; i <= table.getPropertyCount() -1 ; i++)
					{

						//////// System.out.println("table - prop count: "+ table.getPropertyCount());
						tableRow = (SoapObject) table.getProperty(i);
						//////// System.out.println("i value: "+ i);

						RetailerName = tableRow.getProperty("RetailerName").toString().trim();

						Sales200g = tableRow.getProperty("Sales200g").toString().trim();

						Free200g = tableRow.getProperty("Free200g").toString().trim();

						Sample200g = tableRow.getProperty("Sample200g").toString().trim();

						Sales400g = tableRow.getProperty("Sales400g").toString().trim();

						Free400g = tableRow.getProperty("Free400g").toString().trim();

						Sample400g = tableRow.getProperty("Sample400g").toString().trim();

						OrderQtyTotal = tableRow.getProperty("OrderQtyTotal").toString().trim();

						TotalKgs = tableRow.getProperty("TotalKgs").toString().trim();

						////// System.out.println("RetailerName: in service worker " + RetailerName);
						////// System.out.println("Sales200g: " + Sales200g);
						////// System.out.println("Free200g: " + Free200g);
						////// System.out.println("Sample200g: " + Sample200g);
						////// System.out.println("Sales400g: " + Sales400g);
						////// System.out.println("Free400g: " + Free400g);
						////// System.out.println("Sample400g: " + Sample400g);
						////// System.out.println("OrderQtyTotal: " + OrderQtyTotal);
						////// System.out.println("TotalKgs: " + TotalKgs);

						dbengine.SavetblSyncSummuryForEachStoresDetails(RetailerName,Sales200g,Free200g,Sample200g,Sales400g,Free400g,Sample400g,OrderQtyTotal,TotalKgs);


						//dbengine.SavetblSyncSummuryForProductDetails(SkuName,OrderQty,FreeQty,SampleQty,TotalOrderKgs,TotalFreeKgs,TotalSampleKgs,TotalSales);
					}


			}


			dbengine.close();		// #4

			setmovie.director = "1";

			return setmovie;
//return counts;
		} catch (Exception e) {

			// System.out.println("Aman Exception occur in GetSyncSummuryForEachStoreWiseDetails :"+e.toString());
			setmovie.director = e.toString();
			setmovie.movie_name = e.toString();
			dbengine.close();
			return setmovie;
		}

	}
	*/
    public ServiceWorker GetSchemeCoupon(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetSchemeCoupon";
        final String METHOD_NAME = "GetSchemeCoupon";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);
        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

			/*////// System.out.println("soap obj date: "+ dateVAL);

			////// System.out.println("soap obj IMEINo: "+ uuid);

			////// System.out.println("soap obj rID: "+ rID);
			*/
            //client.addProperty("bydate", dateVAL.toString());
            //client.addProperty("IMEINo", uuid.toString());
            //client.addProperty("rID", rID.toString());


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");

            //////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {

                int SchemeID = 0;
                int IsCouponApplicable = 0;
                //////// System.out.println("table.getPropertyCount()"+ table.getPropertyCount());

                if (table.getPropertyCount() != -1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //////// System.out.println("i value: "+ i);

                        SchemeID = Integer.parseInt(tableRow.getProperty("SchemeID").toString().trim());
                        //if(tableRow.getProperty("IsCouponApplicable").toString().trim().equals("true"))
                        //{
                        IsCouponApplicable = Integer.parseInt(tableRow.getProperty("IsCouponApplicable").toString().trim());
                        //}
                        //////// System.out.println("table.getPropertyCount()"+ SchemeID + "^"+IsCouponApplicable);
                        dbengine.fninsertSchemeCoupon(SchemeID, IsCouponApplicable);
                    }
                }
                //dbengine.fninsertSchemeCoupon(18,1);


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 24;
            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetSchemeCoupon :"+e.toString());
            ////// System.out.println("aman getallProduct: 20 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker GetSchemeCouponSlab(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetSchemeCouponSlab";
        final String METHOD_NAME = "GetSchemeCouponSlab";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

			/*////// System.out.println("soap obj date: "+ dateVAL);

			////// System.out.println("soap obj IMEINo: "+ uuid);

			////// System.out.println("soap obj rID: "+ rID);
			*/
            //client.addProperty("bydate", dateVAL.toString());
            //client.addProperty("IMEINo", uuid.toString());
            //client.addProperty("rID", rID.toString());


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");

            //////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {

                int SchemeID = 0;
                int CardSlabID = 0;
                int Slab = 0;
                int Qtny = 0;
                int ProductValueOrSlabBased = 0;
                if (table.getPropertyCount() != -1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);

                        //////// System.out.println("i value: "+ i);

                        SchemeID = Integer.parseInt(tableRow.getProperty("SchemeID").toString().trim());

                        CardSlabID = Integer.parseInt(tableRow.getProperty("CardSlabID").toString().trim());

                        Slab = Integer.parseInt(tableRow.getProperty("Slab").toString().trim());

                        Qtny = Integer.parseInt(tableRow.getProperty("Qtny").toString().trim());
                        if (!tableRow.hasProperty("ValueOrQtyBased")) {
                            ProductValueOrSlabBased = 0;
                        } else {
                            ProductValueOrSlabBased = Integer.parseInt(tableRow.getProperty("ValueOrQtyBased").toString().trim());
                        }


                        dbengine.fninsertSchemeCouponSlab(SchemeID, CardSlabID, Slab, Qtny, ProductValueOrSlabBased);
                    }
                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 25;
            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetSchemeCouponSlab :"+e.toString());
            ////// System.out.println("aman getallProduct: 21 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

	/*public ServiceWorker GetSurveyActiveStatus(Context ctx, String dateVAL, String uuid, String rID) {
		this.context = ctx;

		////// System.out.println("isSurveyActive sunil one start");

		DBAdapter dbengine = new DBAdapter(context);
		dbengine.open();

		final String SOAP_ACTION = "http://tempuri.org/GetSurveyActiveStatus";
		final String METHOD_NAME = "GetSurveyActiveStatus";
		final String NAMESPACE = "http://tempuri.org/";
		final String URL = UrlForWebService;


		SoapObject table = null; // Contains table of dataset that returned
									// through SoapObject
		SoapObject client = null; // Its the client petition to the web service
		SoapObject tableRow = null; // Contains row of table
		SoapObject responseBody = null; // Contains XML content of dataset

		//SoapObject param
		HttpTransportSE transport = null; // That call webservice
		SoapSerializationEnvelope sse = null;



		sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
		// Note if class name isn't "movie" ,you must change
		sse.dotNet = true; // if WebService written .Net is result=true
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				URL,timeout);
		ServiceWorker setmovie = new ServiceWorker();
		try {
			client = new SoapObject(NAMESPACE, METHOD_NAME);

			////// System.out.println("isSurveyActive sunil one111");

			sse.setOutputSoapObject(client);
			sse.bodyOut = client;
			androidHttpTransport.call(SOAP_ACTION, sse);
			////// System.out.println("isSurveyActive sunil one222");
			// This step: get file XML
			responseBody = (SoapObject) sse.getResponse();
			////// System.out.println("isSurveyActive responseBody one :"+responseBody);
			// remove information XML,only retrieved results that returned
			responseBody = (SoapObject) responseBody.getProperty(1);
			//////// System.out.println("isSurveyActive responseBody two :"+responseBody);
			//////// System.out.println("isSurveyActive sunil one333");
			// get information XMl of tables that is returned
			table = (SoapObject) responseBody.getProperty(0);
			//////// System.out.println("isSurveyActive sunil one444");
					// #1

			//dbengine.reCreateDB();
			//////// System.out.println("Debug: " + "dbengine.open");

			//////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
			//dbengine.reCreateDB();
			chkTblStoreListContainsRow=0;
			if(table.getPropertyCount() >= 1 )
			{
				chkTblStoreListContainsRow=1;
			}

			//////// System.out.println("isSurveyActive sunil chkTblStoreListContainsRow : "+chkTblStoreListContainsRow);
			//////// System.out.println("chkTblStoreListContainsRow: "+ chkTblStoreListContainsRow);
			if(chkTblStoreListContainsRow==1)
			{

				int isSurveyActive=0;

				//if(table.getPropertyCount()>=1)
				//{
				//////// System.out.println("isSurveyActive sunil one");
					for(int i = 0; i <= table.getPropertyCount() -1 ; i++)
					{

						//////// System.out.println("table - prop count: "+ table.getPropertyCount());
						//////// System.out.println("isSurveyActive sunil two");
						tableRow = (SoapObject) table.getProperty(i);
						isSurveyActive = Integer.parseInt(tableRow.getProperty("isSurveyActive").toString().trim());

						//////// System.out.println("isSurveyActive sunil :"+isSurveyActive);
						//////// System.out.println("i value: "+ i);
						dbengine.savetblPaneerMstr(isSurveyActive);


						//dbengine.fninsertSchemeCoupon(SchemeID,IsCouponApplicable);
					}
				//}


			}


			dbengine.close();		// #4

			setmovie.director = "1";

			return setmovie;
//return counts;
		} catch (Exception e) {

			// System.out.println("Aman Exception occur in GetSurveyActiveStatus :"+e.toString());
			setmovie.director = e.toString();
			setmovie.movie_name = e.toString();
			dbengine.close();
			return setmovie;
		}

	}
	*/

    public ServiceWorker GetDaySummaryNew(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetDaySummaryNew";
        final String METHOD_NAME = "GetDaySummaryNew";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);

            //////// System.out.println("soap obj IMEINo: "+ uuid);

            //////// System.out.println("soap obj rID: "+ rID);

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

		/*	////// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
			////// System.out.println("Abhinav Raj 121 bydate :"+ dateVAL.toString());
			////// System.out.println("Abhinav Raj 121 rID :"+ rID.toString());*/


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            //////// System.out.println("Debug: " + "dbengine.open");

            //////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {
                int TargetCalls = 0;
                int ActualCallOnRoute = 0;
                int ActualCallsOffROute = 0;
                int ProdCallOnRoute = 0;
                int ProdCallOffRoute = 0;
                Double TargetSalesForDay = 0.00;
                Double TotalSalesForDay = 0.00;
                int CallsRemaining = 0;
                Double TargetSalesMTD = 0.00;
                Double AchivedSalesMDT = 0.00;
                int ProdStoresMTD = 0;
                Double RunRate = 0.00;

                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);

                    TargetCalls = Integer.parseInt(tableRow.getProperty("TargetCalls").toString().trim());

                    ActualCallOnRoute = Integer.parseInt(tableRow.getProperty("ActualCallOnRoute").toString().trim());

                    ActualCallsOffROute = Integer.parseInt(tableRow.getProperty("ActualCallsOffROute").toString().trim());

                    ProdCallOnRoute = Integer.parseInt(tableRow.getProperty("ProdCallOnRoute").toString().trim());

                    ProdCallOffRoute = Integer.parseInt(tableRow.getProperty("ProdCallOffRoute").toString().trim());

                    TargetSalesForDay = Double.parseDouble(tableRow.getProperty("TargetSalesforDay").toString().trim());

                    TotalSalesForDay = Double.parseDouble(tableRow.getProperty("TotalSalesForDay").toString().trim());

                    CallsRemaining = Integer.parseInt(tableRow.getProperty("CallsRemaining").toString().trim());

                    TargetSalesMTD = Double.parseDouble(tableRow.getProperty("TragetSalesMTD").toString().trim());

                    AchivedSalesMDT = Double.parseDouble(tableRow.getProperty("AchivedSalesMTD").toString().trim());

                    ProdStoresMTD = Integer.parseInt(tableRow.getProperty("ProdStoresMTD").toString().trim());
                    RunRate = Double.parseDouble(tableRow.getProperty("RunRate").toString().trim());
                    ////// System.out.println("Abhinav Raj Summary New TargetCalls: " + TargetCalls);
                    ////// System.out.println("Abhinav Raj Summary New ActualCallOnRoute: " + ActualCallOnRoute);
                    ////// System.out.println("Abhinav Raj Summary New ActualCallsOffROute: " + ActualCallsOffROute);
                    ////// System.out.println("Abhinav Raj Summary New ProdCallOnRoute: " + ProdCallOnRoute);
                    ////// System.out.println("Abhinav Raj Summary New ProdCallOffRoute: " + ProdCallOffRoute);
                    ////// System.out.println("Abhinav Raj Summary New TargetSalesForDay: " + TargetSalesForDay);
                    ////// System.out.println("Abhinav Raj Summary New TotalSalesForDay: " + TotalSalesForDay);
                    ////// System.out.println("Abhinav Raj Summary New CallsRemaining: " + CallsRemaining);
                    ////// System.out.println("Abhinav Raj Summary New TargetSalesMTD: " + TargetSalesMTD);
                    ////// System.out.println("Abhinav Raj Summary New AchivedSalesMDT: " + AchivedSalesMDT);
                    ////// System.out.println("Abhinav Raj Summary New ProdStoresMTD: " + ProdStoresMTD);
                    ////// System.out.println("Abhinav Raj Summary New RunRate: " + RunRate);

					/*	////// System.out.println("Abhinav Raj 121 ActualCalls: " + ActualCalls);
						////// System.out.println("Abhinav Raj 121 ProductiveCalls: " + ProductiveCalls);
						////// System.out.println("Abhinav Raj 121 TotSalesValue: " + TotSalesValue);
						////// System.out.println("Abhinav Raj 121 TotKGSales: " + TotKGSales);
						////// System.out.println("Abhinav Raj 121 TotFreeQtyKGSales: " + TotFreeQtyKGSales);
						////// System.out.println("Abhinav Raj 121 TotSampleKGSales: " + TotSampleKGSales);
						//////// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
*/
                    dbengine.insertTblDaySummaryNew(TargetCalls, ActualCallOnRoute, ActualCallsOffROute, ProdCallOnRoute, ProdCallOffRoute, TargetSalesForDay, TotalSalesForDay, CallsRemaining, TargetSalesMTD, AchivedSalesMDT, ProdStoresMTD, RunRate);

                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 26;
            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetDaySummaryNew :"+e.toString());
            //////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker callInvoiceButtonStoreMstr(Context ctx, String dateVAL, String uuid, String rID, HashMap<String, String> hmapInvoiceOrderIDandStatusNew) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();


        final String SOAP_ACTION = "http://tempuri.org/GetInvoiceButtonStoreMstr";
        final String METHOD_NAME = "GetInvoiceButtonStoreMstr";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            ////// System.out.println("Debug: " + "dbengine.open");

            ////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {
                String StoreID = "0";
                String StoreName = "NA";
                String RouteID = "0";
                String RouteName = "NA";
                String DistID = "0";
                String DistName = "NA";
                String InvoiceForDate = "0";
                String flgSubmit = "0";
                String OrderID = "0";


                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if (tableRow.hasProperty("StoreID")) {
                        if (tableRow.getProperty("StoreID").toString().isEmpty()) {
                            StoreID = "0";
                        } else {
                            StoreID = tableRow.getProperty("StoreID").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("StoreName")) {
                        if (tableRow.getProperty("StoreName").toString().isEmpty()) {
                            StoreName = "NA";
                        } else {
                            StoreName = tableRow.getProperty("StoreName").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("RouteID")) {
                        if (tableRow.getProperty("RouteID").toString().isEmpty()) {
                            RouteID = "0";
                        } else {
                            RouteID = tableRow.getProperty("RouteID").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("RouteName")) {
                        if (tableRow.getProperty("RouteName").toString().isEmpty()) {
                            RouteName = "NA";
                        } else {
                            RouteName = tableRow.getProperty("RouteName").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("DistID")) {
                        if (tableRow.getProperty("DistID").toString().isEmpty()) {
                            DistID = "0";
                        } else {
                            DistID = tableRow.getProperty("DistID").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("DistName")) {
                        if (tableRow.getProperty("DistName").toString().isEmpty()) {
                            DistName = "NA";
                        } else {
                            DistName = tableRow.getProperty("DistName").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("InvoiceForDate")) {
                        if (tableRow.getProperty("InvoiceForDate").toString().isEmpty()) {
                            InvoiceForDate = "0";
                        } else {
                            InvoiceForDate = tableRow.getProperty("InvoiceForDate").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("flgSubmit")) {
                        if (tableRow.getProperty("flgSubmit").toString().isEmpty()) {
                            flgSubmit = "NA";
                        } else {
                            flgSubmit = tableRow.getProperty("flgSubmit").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("OrderID")) {
                        if (tableRow.getProperty("OrderID").toString().isEmpty()) {
                            OrderID = "0";
                        } else {
                            OrderID = tableRow.getProperty("OrderID").toString().trim();

                        }
                    }

                    //// System.out.println("Ajay testing StoreID :"+StoreID);
                    //// System.out.println("Ajay testing  OrderID :"+OrderID);

						/*dbengine.inserttblInvoiceButtonStoreMstr(StoreID,StoreName,RouteID,
								RouteName,DistID,DistName,InvoiceForDate,flgSubmit,uuid.toString(),OrderID);*/


                    if (hmapInvoiceOrderIDandStatusNew.containsKey(OrderID)) {
                        if (hmapInvoiceOrderIDandStatusNew.get(OrderID).equals("4")) {
                            //dbengine.fnDeletetblInvoiceSubmittedRecords(OrderID);
                        } else {
                            dbengine.fnDeletetblInvoiceSubmittedRecords(OrderID);
                            dbengine.inserttblInvoiceButtonStoreMstr(StoreID, StoreName, RouteID, RouteName, DistID, DistName, InvoiceForDate, flgSubmit, uuid.toString(), OrderID);
                        }
                    } else {
                        dbengine.inserttblInvoiceButtonStoreMstr(StoreID, StoreName, RouteID, RouteName, DistID, DistName, InvoiceForDate, flgSubmit, uuid.toString(), OrderID);
                    }

                }


            }

            dbengine.fnDeleteUnWantedSubmitedInvoiceOrders();
            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetInvoiceButtonStoreMstr :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }
    }

    public ServiceWorker callInvoiceButtonProductMstr(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetInvoiceButtonProductMstr";
        final String METHOD_NAME = "GetInvoiceButtonProductMstr";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

// System.out.println("Abhinav Raj and Nithis Imei No="+uuid);
// System.out.println("Abhinav Raj and Nithis Route Id="+rID);
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);


            dbengine.fnDeletetblInvoiceButtonProductMstr();

            //dbengine.reCreateDB();
            ////// System.out.println("Debug: " + "dbengine.open");

            ////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {
                String ProductId = "0";
                String ProductName = "NA";

                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if (tableRow.hasProperty("ProductId")) {
                        if (tableRow.getProperty("ProductId").toString().isEmpty()) {
                            ProductId = "0";
                        } else {
                            ProductId = tableRow.getProperty("ProductId").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("ProductName")) {
                        if (tableRow.getProperty("ProductName").toString().isEmpty()) {
                            ProductName = "NA";
                        } else {
                            ProductName = tableRow.getProperty("ProductName").toString().trim();

                        }
                    }


                    dbengine.inserttblInvoiceButtonProductMstr(ProductId, ProductName);
                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
            //return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetInvoiceButtonProductMstr :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }


    public ServiceWorker callInvoiceButtonStoreProductwiseOrder(Context ctx, String dateVAL, String uuid, String rID, HashMap<String, String> hmapInvoiceOrderIDandStatusNew) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetInvoiceButtonStoreProductwiseOrder";
        final String METHOD_NAME = "GetInvoiceButtonStoreProductwiseOrder";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);


            //dbengine.reCreateDB();
            ////// System.out.println("Debug: " + "dbengine.open");

            ////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {
                String StoreID = "0";
                String ProductID = "0";
                String OrderQty = "0";
                String ProductPrice = "NA";
                String InvoiceForDate = "0";
                String OrderID = "0";
                String CatID = "0";
                int Freeqty = 0;
                double TotLineDiscVal = 0.0;

                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if (tableRow.hasProperty("StoreID")) {
                        if (tableRow.getProperty("StoreID").toString().isEmpty()) {
                            StoreID = "0";
                        } else {
                            StoreID = tableRow.getProperty("StoreID").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("ProductID")) {
                        if (tableRow.getProperty("ProductID").toString().isEmpty()) {
                            ProductID = "0";
                        } else {
                            ProductID = tableRow.getProperty("ProductID").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("OrderQty")) {
                        if (tableRow.getProperty("OrderQty").toString().isEmpty()) {
                            OrderQty = "0";
                        } else {
                            OrderQty = tableRow.getProperty("OrderQty").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("ProductPrice")) {
                        if (tableRow.getProperty("ProductPrice").toString().isEmpty()) {
                            ProductPrice = "0";
                        } else {
                            ProductPrice = tableRow.getProperty("ProductPrice").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("InvoiceForDate")) {
                        if (tableRow.getProperty("InvoiceForDate").toString().isEmpty()) {
                            InvoiceForDate = "0";
                        } else {
                            InvoiceForDate = tableRow.getProperty("InvoiceForDate").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("OrderID")) {
                        if (tableRow.getProperty("OrderID").toString().isEmpty()) {
                            OrderID = "0";
                        } else {
                            OrderID = tableRow.getProperty("OrderID").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("CatID")) {
                        if (tableRow.getProperty("CatID").toString().isEmpty()) {
                            CatID = "0";
                        } else {
                            CatID = tableRow.getProperty("CatID").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("Freeqty")) {
                        if (tableRow.getProperty("Freeqty").toString().isEmpty()) {
                            Freeqty = 0;
                        } else {
                            Freeqty = Integer.parseInt(tableRow.getProperty("Freeqty").toString().trim());

                        }
                    }

                    if (tableRow.hasProperty("TotLineDiscVal")) {
                        if (tableRow.getProperty("TotLineDiscVal").toString().isEmpty()) {
                            TotLineDiscVal = 0.00;
                        } else {
                            TotLineDiscVal = Double.parseDouble(tableRow.getProperty("TotLineDiscVal").toString().trim());

                        }
                    }
                    //TotLineDiscVal
                    //	// System.out.println("Ajay testing order StoreID :"+StoreID);
                    //	// System.out.println("Ajay testing order OrderID :"+OrderID);




						/*dbengine.inserttblInvoiceButtonStoreProductwiseOrder(StoreID,ProductID,OrderQty,
								ProductPrice,InvoiceForDate,OrderID,CatID,Freeqty,TotLineDiscVal);*/



						/*if(hmapInvoiceOrderIDandStatusNew.containsKey(OrderID))
					      {
					      }
					      else
					      {
					      dbengine.inserttblInvoiceButtonStoreProductwiseOrder(StoreID,ProductID,OrderQty,ProductPrice,InvoiceForDate,OrderID,CatID,Freeqty,TotLineDiscVal);
					      }*/

                    if (hmapInvoiceOrderIDandStatusNew.containsKey(OrderID)) {
                        if (hmapInvoiceOrderIDandStatusNew.get(OrderID).equals("4")) {
                            //dbengine.fnDeletetblInvoiceSubmittedRecords(OrderID);
                        } else {

                            dbengine.inserttblInvoiceButtonStoreProductwiseOrder(StoreID, ProductID, OrderQty, ProductPrice, InvoiceForDate, OrderID, CatID, Freeqty, TotLineDiscVal);
                        }
                    } else {
                        dbengine.inserttblInvoiceButtonStoreProductwiseOrder(StoreID, ProductID, OrderQty, ProductPrice, InvoiceForDate, OrderID, CatID, Freeqty, TotLineDiscVal);
                    }


                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
//return counts;
        } catch (Exception e) {


            // System.out.println("Aman Exception occur in GetInvoiceButtonStoreProductwiseOrder :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker GetOrderDetailsOnLastSalesSummary(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetLastOrderDetailsOnOldSummary";
        final String METHOD_NAME = "GetLastOrderDetailsOnOldSummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            ////// System.out.println("soap obj date: "+ dateVAL);

            ////// System.out.println("soap obj IMEINo: "+ uuid);

            ////// System.out.println("soap obj rID: "+ rID);

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

		/*	//// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
			//// System.out.println("Abhinav Raj 121 bydate :"+ dateVAL.toString());
			//// System.out.println("Abhinav Raj 121 rID :"+ rID.toString());*/


            /*client.addProperty("IMEINo", uuid.toString());*/
            // System.out.println("Ashish and Anuj 11");
            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // System.out.println("Ashish and Anuj 12");
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);
            // System.out.println("Ashish and Anuj 13");
            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // System.out.println("Ashish and Anuj 4");

            // #1

            //dbengine.reCreateDB();
            ////// System.out.println("Debug: " + "dbengine.open");

            ////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {
                String StoreID;
                String Date;
                String SKUID;
                int Order = 0;
                String Free = "0";
                int Stock = 0;
                String SKUName;


				/*<xs:element name="Order" minOccurs="0" type="xs:int"/>

				<xs:element name="Free" minOccurs="0" type="xs:decimal"/>

				<xs:element name="Stock" minOccurs="0" type="xs:int"/>

				<xs:element name="SKUName" minOccurs="0" type="xs:string"/>*/

                // System.out.println("Ashish and Anuj 1");


                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                    // System.out.println("Ashish and Anuj 2");
                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);
                    // System.out.println("Ashish and Anuj 3");
                    if ((!tableRow.hasProperty("StoreID"))) {
                        StoreID = "0";
                    } else {
                        StoreID = tableRow.getProperty("StoreID").toString().trim();
                    }
                    // System.out.println("Ashish and Anuj 14 StoreID :"+StoreID);
                    if ((!tableRow.hasProperty("Date"))) {
                        Date = "0";
                    } else {
                        Date = tableRow.getProperty("Date").toString().trim();
                    }
                    // System.out.println("Ashish and Anuj 15 Date :"+Date);
                    if ((!tableRow.hasProperty("SKUID"))) {
                        SKUID = "0";
                    } else {
                        SKUID = tableRow.getProperty("SKUID").toString().trim();
                    }
                    // System.out.println("Ashish and Anuj 16 SKUID :"+SKUID);
                    if ((!tableRow.hasProperty("Order"))) {
                        Order = 0;
                    } else {
                        Order = Integer.parseInt(tableRow.getProperty("Order").toString().trim());
                    }
                    // System.out.println("Ashish and Anuj 16 Order :"+Order);
                    if ((!tableRow.hasProperty("Free"))) {
                        Free = "0";
                    } else {
                        Free = tableRow.getProperty("Free").toString().trim();
                    }
                    // System.out.println("Ashish and Anuj 18 Free :"+Free);
                    if ((!tableRow.hasProperty("Stock"))) {
                        Stock = 0;
                    } else {
                        Stock = Integer.parseInt(tableRow.getProperty("Stock").toString().trim());
                    }

                    // System.out.println("Ashish and Anuj 18 Stock :"+Stock);
                    if ((!tableRow.hasProperty("SKUName"))) {
                        SKUName = "0";
                    } else {
                        SKUName = tableRow.getProperty("SKUName").toString().trim();
                    }
                    // System.out.println("Ashish and Anuj 18 Free :"+Free);

                    // System.out.println("Ashish and Anuj Geting 1 2 StoreID  :"+StoreID);
                    // System.out.println("Ashish and Anuj Geting 1 2 Date :"+Date);
                    // System.out.println("Ashish and Anuj Geting 1 2 SKUID : "+SKUID);
                    // System.out.println("Ashish and Anuj Geting 1 2 Order : "+Order);
                    // System.out.println("Ashish and Anuj Geting 1 2 Free : "+Free);
                    // System.out.println("Ashish and Anuj Geting 1 2 Stock : "+Stock);
                    // System.out.println("Ashish and Anuj Geting 1 2 SKUName : "+SKUName);


                    dbengine.inserttblFirstOrderDetailsOnLastVisitDetailsActivity(StoreID, Date, SKUID, Order, "" + Free, Stock, SKUName);


                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 27;
            return setmovie;
//return counts;
        } catch (Exception e) {


            //// System.out.println("Aman Exception occur in GetSyncSummuryDetails :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }


    public ServiceWorker GetVisitDetailsOnLastSalesSummary(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetLastVisitDetailsOnOldSummary";
        final String METHOD_NAME = "GetLastVisitDetailsOnOldSummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            ////// System.out.println("soap obj date: "+ dateVAL);

            ////// System.out.println("soap obj IMEINo: "+ uuid);

            ////// System.out.println("soap obj rID: "+ rID);

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());

		/*	//// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
			//// System.out.println("Abhinav Raj 121 bydate :"+ dateVAL.toString());
			//// System.out.println("Abhinav Raj 121 rID :"+ rID.toString());*/


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //dbengine.reCreateDB();
            ////// System.out.println("Debug: " + "dbengine.open");

            ////// System.out.println("chkTblStoreListContainsRow In Product: "+ chkTblStoreListContainsRow);
            //dbengine.reCreateDB();
            if (chkTblStoreListContainsRow == 1) {
                String StoreID;
                String Date;
                String SKUID;

                int Stock = 0;
                String SKUName;

				/*<xs:element name="StoreID" minOccurs="0" type="xs:int"/>

				<xs:element name="Date" minOccurs="0" type="xs:dateTime"/>

				<xs:element name="SKUID" minOccurs="0" type="xs:int"/>

				<xs:element name="Stock" minOccurs="0" type="xs:int"/>

				<xs:element name="SKUName" minOccurs="0" type="xs:string"/>

				*/


                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if ((!tableRow.hasProperty("StoreID"))) {
                        StoreID = "0";
                    } else {
                        StoreID = tableRow.getProperty("StoreID").toString().trim();
                    }
                    if ((!tableRow.hasProperty("Date"))) {
                        Date = "0";
                    } else {
                        Date = tableRow.getProperty("Date").toString().trim();
                    }

                    if ((!tableRow.hasProperty("SKUID"))) {
                        SKUID = "0";
                    } else {
                        SKUID = tableRow.getProperty("SKUID").toString().trim();
                    }


                    if ((!tableRow.hasProperty("Stock"))) {
                        Stock = 0;
                    } else {
                        Stock = Integer.parseInt(tableRow.getProperty("Stock").toString().trim());
                    }
                    if ((!tableRow.hasProperty("SKUName"))) {
                        SKUName = "0";
                    } else {
                        SKUName = tableRow.getProperty("SKUName").toString().trim();
                    }


                    // System.out.println("Aaaj insert  value table StoreID "+StoreID);
                    // System.out.println("Aaaj insert  value table Date "+Date);
                    // System.out.println("Aaaj insert  value table SKUID "+SKUID);
                    // System.out.println("Aaaj insert  value table Stock "+Stock);
                    // System.out.println("Aaaj insert  value table SKUName "+SKUName);
                    dbengine.inserttblSecondVisitDetailsOnLastVisitDetailsActivity(StoreID, Date, SKUID, Stock, SKUName);

                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 28;
            return setmovie;
//return counts;
        } catch (Exception e) {


            //// System.out.println("Aman Exception occur in GetSyncSummuryDetails :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker GetLODDetailsOnLastSalesSummary(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetLODQtyOnOldSummary";
        final String METHOD_NAME = "GetLODQtyOnOldSummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            ////// System.out.println("soap obj date: "+ dateVAL);

            ////// System.out.println("soap obj IMEINo: "+ uuid);

            ////// System.out.println("soap obj rID: "+ rID);
            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);
		/*	//// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
			//// System.out.println("Abhinav Raj 121 bydate :"+ dateVAL.toString());
			//// System.out.println("Abhinav Raj 121 rID :"+ rID.toString());*/


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();


            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }

            int chkLODDetailsOnLastSalesSummaryContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkLODDetailsOnLastSalesSummaryContainsRow = 1;
            }
            //dbengine.reCreateDB();
            if (chkLODDetailsOnLastSalesSummaryContainsRow == 1) {
                String StoreID;
                String Date;
                String SKUID;

                int Qty = 0;
                String SKUName;


				/*<xs:element name="StoreID" minOccurs="0" type="xs:int"/>

				<xs:element name="SKUID" minOccurs="0" type="xs:int"/>

				<xs:element name="Date" minOccurs="0" type="xs:dateTime"/>

				<xs:element name="Qty" minOccurs="0" type="xs:int"/>

				<xs:element name="SKUName" minOccurs="0" type="xs:string"/>*/


                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if ((!tableRow.hasProperty("StoreID"))) {
                        StoreID = "0";
                    } else {
                        StoreID = tableRow.getProperty("StoreID").toString().trim();
                    }
                    if ((!tableRow.hasProperty("Date"))) {
                        Date = "0";
                    } else {
                        Date = tableRow.getProperty("Date").toString().trim();
                    }

                    if ((!tableRow.hasProperty("SKUID"))) {
                        SKUID = "0";
                    } else {
                        SKUID = tableRow.getProperty("SKUID").toString().trim();
                    }


                    if ((!tableRow.hasProperty("Qty"))) {
                        Qty = 0;
                    } else {
                        Qty = Integer.parseInt(tableRow.getProperty("Qty").toString().trim());
                    }
                    if ((!tableRow.hasProperty("SKUName"))) {
                        SKUName = "0";
                    } else {
                        SKUName = tableRow.getProperty("SKUName").toString().trim();
                    }


                    dbengine.inserttblLODOnLastSalesSummary(StoreID, Date, SKUID, Qty, SKUName);
                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 29;
            return setmovie;
//return counts;
        } catch (Exception e) {


            //// System.out.println("Aman Exception occur in GetSyncSummuryDetails :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }


    public ServiceWorker GetCallspForPDAGetLastVisitDate(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/CallspForPDAGetLastVisitDate";
        final String METHOD_NAME = "CallspForPDAGetLastVisitDate";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            ////// System.out.println("soap obj date: "+ dateVAL);

            ////// System.out.println("soap obj IMEINo: "+ uuid);

            ////// System.out.println("soap obj rID: "+ rID);
            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }
            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);
		/*	//// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
			//// System.out.println("Abhinav Raj 121 bydate :"+ dateVAL.toString());
			//// System.out.println("Abhinav Raj 121 rID :"+ rID.toString());*/


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();


            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }

            int chkTblLastVisitDateContainsRow = 0;
            if (soDiffg.hasProperty("NewDataSet")) {
                chkTblLastVisitDateContainsRow = 1;
            }
            //dbengine.reCreateDB();
            if (chkTblLastVisitDateContainsRow == 1) {
                String StoreID;
                String VisitDate;
                String flgOrder;


                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if ((!tableRow.hasProperty("StoreID"))) {
                        StoreID = "0";
                    } else {
                        StoreID = tableRow.getProperty("StoreID").toString().trim();
                    }
                    if ((!tableRow.hasProperty("VIsitDate"))) {
                        VisitDate = "0";
                    } else {
                        VisitDate = tableRow.getProperty("VIsitDate").toString().trim();
                    }

                    if ((!tableRow.hasProperty("flgOrder"))) {
                        flgOrder = "0";
                    } else {
                        flgOrder = tableRow.getProperty("flgOrder").toString().trim();
                    }


                    dbengine.inserttblForPDAGetLastVisitDate(StoreID, VisitDate, flgOrder);
                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 31;
            return setmovie;
//return counts;
        } catch (Exception e) {


            //// System.out.println("Aman Exception occur in GetSyncSummuryDetails :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker GetCallspForPDAGetLastOrderDate(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/CallspForPDAGetLastOrderDate";
        final String METHOD_NAME = "CallspForPDAGetLastOrderDate";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            ////// System.out.println("soap obj date: "+ dateVAL);

            ////// System.out.println("soap obj IMEINo: "+ uuid);

            ////// System.out.println("soap obj rID: "+ rID);

            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();


            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }

            int chkTblLastOrderDateContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkTblLastOrderDateContainsRow = 1;
            }
            //dbengine.reCreateDB();
            if (chkTblLastOrderDateContainsRow == 1) {
                String StoreID;
                String OrderDate;
                String flgExecutionSummary;


                //	inserttblForPDAGetLastOrderDate(String StoreID,String OrderDate,String flgExecutionSummary)


                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if ((!tableRow.hasProperty("StoreID"))) {
                        StoreID = "0";
                    } else {
                        StoreID = tableRow.getProperty("StoreID").toString().trim();
                    }
                    if ((!tableRow.hasProperty("OrderDate"))) {
                        OrderDate = "0";
                    } else {
                        OrderDate = tableRow.getProperty("OrderDate").toString().trim();
                    }

                    if ((!tableRow.hasProperty("flgExecutionSummary"))) {
                        flgExecutionSummary = "0";
                    } else {
                        flgExecutionSummary = tableRow.getProperty("flgExecutionSummary").toString().trim();
                    }


                    dbengine.inserttblForPDAGetLastOrderDate(StoreID, OrderDate, flgExecutionSummary);
                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 32;
            return setmovie;
//return counts;
        } catch (Exception e) {


            //// System.out.println("Aman Exception occur in GetSyncSummuryDetails :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker GetCallspForPDAGetLastVisitDetails(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/CallspForPDAGetLastVisitDetails";
        final String METHOD_NAME = "CallspForPDAGetLastVisitDetails";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            ////// System.out.println("soap obj date: "+ dateVAL);

            ////// System.out.println("soap obj IMEINo: "+ uuid);

            ////// System.out.println("soap obj rID: "+ rID);
            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);
		/*	//// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
			//// System.out.println("Abhinav Raj 121 bydate :"+ dateVAL.toString());
			//// System.out.println("Abhinav Raj 121 rID :"+ rID.toString());*/


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();


            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }

            int chkLastVisitDetailsContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkLastVisitDetailsContainsRow = 1;
            }

            if (chkLastVisitDetailsContainsRow == 1) {
                String StoreID;
                String Date;
                String Order;
                String Stock;
                String SKUName;
                String ExecutionQty;


                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if ((!tableRow.hasProperty("StoreID"))) {
                        StoreID = "0";
                    } else {
                        StoreID = tableRow.getProperty("StoreID").toString().trim();
                    }
                    if ((!tableRow.hasProperty("Date"))) {
                        Date = "0";
                    } else {
                        Date = tableRow.getProperty("Date").toString().trim();
                    }

                    if ((!tableRow.hasProperty("Order"))) {
                        Order = "0";
                    } else {
                        Order = tableRow.getProperty("Order").toString().trim();
                    }
                    if ((!tableRow.hasProperty("Stock"))) {
                        Stock = "0";
                    } else {
                        Stock = tableRow.getProperty("Stock").toString().trim();
                    }
                    if ((!tableRow.hasProperty("SKUName"))) {
                        SKUName = "0";
                    } else {
                        SKUName = tableRow.getProperty("SKUName").toString().trim();
                    }

                    if ((!tableRow.hasProperty("ExecutionQty"))) {
                        ExecutionQty = "0";
                    } else {
                        ExecutionQty = tableRow.getProperty("ExecutionQty").toString().trim();
                    }


                    // System.out.println("Check Ajeet Value StoreID :"+StoreID);

                    dbengine.inserttblForPDAGetLastVisitDetails(StoreID, Date, Order, Stock, SKUName, ExecutionQty);
                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 33;
            return setmovie;
//return counts;
        } catch (Exception e) {


            //// System.out.println("Aman Exception occur in GetSyncSummuryDetails :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            flagExecutedServiceSuccesfully = 0;
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker GetCallspForPDAGetLastOrderDetails(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/CallspForPDAGetLastOrderDetails";
        final String METHOD_NAME = "CallspForPDAGetLastOrderDetails";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }


            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();

            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }

            int chkLastOrderDetailsContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkLastOrderDetailsContainsRow = 1;
            }
            //dbengine.reCreateDB();
            if (chkLastOrderDetailsContainsRow == 1) {
                String StoreID;
                String OrderDate;
                String ProductID;
                String OrderQty;
                String FreeQty;
                String PrdName;
                String ExecutionQty;


                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if ((!tableRow.hasProperty("StoreID"))) {
                        StoreID = "0";
                    } else {
                        StoreID = tableRow.getProperty("StoreID").toString().trim();
                    }
                    if ((!tableRow.hasProperty("OrderDate"))) {
                        OrderDate = "0";
                    } else {
                        OrderDate = tableRow.getProperty("OrderDate").toString().trim();
                    }

                    if ((!tableRow.hasProperty("ProductID"))) {
                        ProductID = "0";
                    } else {
                        ProductID = tableRow.getProperty("ProductID").toString().trim();
                    }
                    if ((!tableRow.hasProperty("OrderQty"))) {
                        OrderQty = "0";
                    } else {
                        OrderQty = tableRow.getProperty("OrderQty").toString().trim();
                    }
                    if ((!tableRow.hasProperty("FreeQty"))) {
                        FreeQty = "0";
                    } else {
                        FreeQty = tableRow.getProperty("FreeQty").toString().trim();
                    }

                    if ((!tableRow.hasProperty("PrdName"))) {
                        PrdName = "0";
                    } else {
                        PrdName = tableRow.getProperty("PrdName").toString().trim();
                    }
                    if ((!tableRow.hasProperty("ExecutionQty"))) {
                        ExecutionQty = "0";
                    } else {
                        ExecutionQty = tableRow.getProperty("ExecutionQty").toString().trim();
                    }


                    dbengine.inserttblForPDAGetLastOrderDetails(StoreID, OrderDate, ProductID, OrderQty, FreeQty, PrdName, ExecutionQty);
                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 34;
            return setmovie;
//return counts;
        } catch (Exception e) {


            //// System.out.println("Aman Exception occur in GetSyncSummuryDetails :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker GetCallspForPDAGetLastOrderDetails_TotalValues(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/CallspForPDAGetLastOrderDetails_TotalValues";
        final String METHOD_NAME = "CallspForPDAGetLastOrderDetails_TotalValues";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            ////// System.out.println("soap obj date: "+ dateVAL);

            ////// System.out.println("soap obj IMEINo: "+ uuid);

            ////// System.out.println("soap obj rID: "+ rID);
            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);
		/*	//// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
			//// System.out.println("Abhinav Raj 121 bydate :"+ dateVAL.toString());
			//// System.out.println("Abhinav Raj 121 rID :"+ rID.toString());*/


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();

            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }


            int chkLastOrderDetails_TotalValuesContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkLastOrderDetails_TotalValuesContainsRow = 1;
            }

            if (chkLastOrderDetails_TotalValuesContainsRow == 1) {
                String StoreID;
                String OrderValue;
                String ExecutionValue;


                //


                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if ((!tableRow.hasProperty("StoreID"))) {
                        StoreID = "0";
                    } else {
                        StoreID = tableRow.getProperty("StoreID").toString().trim();
                    }
                    if ((!tableRow.hasProperty("OrderValue"))) {
                        OrderValue = "0";
                    } else {
                        OrderValue = tableRow.getProperty("OrderValue").toString().trim();
                    }

                    if ((!tableRow.hasProperty("ExecutionValue"))) {
                        ExecutionValue = "0";
                    } else {
                        ExecutionValue = tableRow.getProperty("ExecutionValue").toString().trim();
                    }


                    dbengine.inserttblspForPDAGetLastOrderDetails_TotalValues(StoreID, OrderValue, ExecutionValue);
                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 35;
            return setmovie;
//return counts;
        } catch (Exception e) {


            //// System.out.println("Aman Exception occur in GetSyncSummuryDetails :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker GetCallspForPDAGetExecutionSummary(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/CallspForPDAGetExecutionSummary";
        final String METHOD_NAME = "CallspForPDAGetExecutionSummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);

		/*	//// System.out.println("Abhinav Raj 121 IMEINo :"+ uuid.toString());
			//// System.out.println("Abhinav Raj 121 bydate :"+ dateVAL.toString());
			//// System.out.println("Abhinav Raj 121 rID :"+ rID.toString());*/


            /*client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();


            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }


            int chkExecutionSummaryContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkExecutionSummaryContainsRow = 1;
            }

            if (chkExecutionSummaryContainsRow == 1) {
                String StoreID;
                String OrderDate;
                String ProductID;
                String OrderQty;
                String flgInvStatus;
                String ProductQty;
                String PrdName;
                Date OrderDate1;


                //inserttblForPDAGetExecutionSummary(String StoreID,String OrderDate,String ProductID,String OrderQty,
                //String flgInvStatus,
                //String ProductQty)


                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                    ////// System.out.println("table - prop count: "+ table.getPropertyCount());
                    tableRow = (SoapObject) table.getProperty(i);
                    ////// System.out.println("i value: "+ i);

                    if ((!tableRow.hasProperty("StoreID"))) {
                        StoreID = "0";
                    } else {
                        StoreID = tableRow.getProperty("StoreID").toString().trim();
                    }
                    if ((!tableRow.hasProperty("OrderDate"))) {
                        OrderDate = "0";
                    } else {
                        OrderDate = tableRow.getProperty("OrderDate").toString().trim();
                    }

                    if ((!tableRow.hasProperty("ProductID"))) {
                        ProductID = "0";
                    } else {
                        ProductID = tableRow.getProperty("ProductID").toString().trim();
                    }
                    if ((!tableRow.hasProperty("OrderQty"))) {
                        OrderQty = "0";
                    } else {
                        OrderQty = tableRow.getProperty("OrderQty").toString().trim();
                    }
                    if ((!tableRow.hasProperty("flgInvStatus"))) {
                        flgInvStatus = "0";
                    } else {
                        flgInvStatus = tableRow.getProperty("flgInvStatus").toString().trim();
                    }
                    if ((!tableRow.hasProperty("ProductQty"))) {
                        ProductQty = "0";
                    } else {
                        ProductQty = tableRow.getProperty("ProductQty").toString().trim();
                    }
                    if ((!tableRow.hasProperty("PrdName"))) {
                        PrdName = "0";
                    } else {
                        PrdName = tableRow.getProperty("PrdName").toString().trim();
                    }


                    dbengine.inserttblForPDAGetExecutionSummary(StoreID, OrderDate, ProductID, OrderQty, flgInvStatus, ProductQty, PrdName);
                }


            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 36;
            return setmovie;
//return counts;
        } catch (Exception e) {


            //// System.out.println("Aman Exception occur in GetSyncSummuryDetails :"+e.toString());
            ////// System.out.println("aman getallProduct: 16 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }


    public ServiceWorker getStoreTypeMstr(Context ctx, String dateVAL, String uuid) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetStoreTypeMaster";
        final String METHOD_NAME = "GetStoreTypeMaster";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("CityId", 1);

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            //// System.out.println("Aman a8 :"+responseBody);
            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }


            int chkTblStoreTypeContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkTblStoreTypeContainsRow = 1;
                dbengine.deletetblStoreTypeMstr();
            }


            if (chkTblStoreTypeContainsRow == 1) {
                int AutoIdStore = 0;
                if (table.getPropertyCount() > 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        // // System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //// System.out.println("i value: "+ i);

                        int StoreTypeID = 0;
                        String StoreTypeDescr = "NA";

						/*<xs:element name="ID" minOccurs="0" type="xs:int"/>

						<xs:element name="Descr" minOccurs="0" type="xs:string"/>*/

                        if (tableRow.hasProperty("ID")) {
                            //// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("ID").toString().isEmpty()) {
                                StoreTypeID = 0;
                            } else {
                                String abc = tableRow.getProperty("ID").toString().trim();
                                StoreTypeID = Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("Descr")) {
                            //// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("Descr").toString().isEmpty()) {
                                StoreTypeDescr = "NA";
                            } else {
                                StoreTypeDescr = tableRow.getProperty("Descr").toString().trim();
                            }
                        }


                        AutoIdStore = i + 1;

                        // System.out.println("Arjun Golu StoreType Mstr Data from Server :"+"StoreTypeID :"+StoreTypeID+"StoreTypeDescr :"+StoreTypeDescr);

                        dbengine.savetblStoreTypeMstr(AutoIdStore, StoreTypeID, StoreTypeDescr);


                    }
                }

            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 37;
            return setmovie;
//return counts;
        } catch (Exception e) {

            // System.out.println("Arjun getStoreTypeMstr: 2 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }


    public ServiceWorker gettblTradeChannelMstr(Context ctx, String dateVAL, String uuid) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/GetTradeChannels";
        final String METHOD_NAME = "GetTradeChannels";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("CityId", 1);

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            //// System.out.println("TradeChannel  a8 :"+responseBody);
            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }


            int chkTblTradeChannelContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkTblTradeChannelContainsRow = 1;
                dbengine.deletetblTradeChannelMstr();
            }


            if (chkTblTradeChannelContainsRow == 1) {
                int AutoIdStore = 0;
                if (table.getPropertyCount() > 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        // // System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //// System.out.println("i value: "+ i);

                        int TradeChannelID = 0;
                        String TradeChannelName = "NA";

						/*<xs:element name="TradeChannelID" minOccurs="0" type="xs:int"/>

						<xs:element name="TradeChannelName" minOccurs="0" type="xs:string"/>*/

                        //// System.out.println("TradeChannel a10 :"+table.getPropertyCount());


                        if (tableRow.hasProperty("TradeChannelID")) {
                            //// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("TradeChannelID").toString().isEmpty()) {
                                TradeChannelID = 0;
                            } else {
                                String abc = tableRow.getProperty("TradeChannelID").toString().trim();
                                TradeChannelID = Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("TradeChannelName")) {
                            //// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("TradeChannelName").toString().isEmpty()) {
                                TradeChannelName = "NA";
                            } else {
                                TradeChannelName = tableRow.getProperty("TradeChannelName").toString().trim();
                            }
                        }


                        AutoIdStore = i + 1;
                        //// System.out.println("TradeChannel from Server :"+"AutoIdStore :"+AutoIdStore+"TradeChannelID :"+TradeChannelID+"TradeChannelName :"+TradeChannelName);


                        dbengine.savetblTradeChannelMstr(AutoIdStore, TradeChannelID, TradeChannelName);


                    }
                }

            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 38;
            return setmovie;
//return counts;
        } catch (Exception e) {

            // System.out.println("Arjun getStoreTypeMstr: 2 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getStoreProductClassificationTypeListMstr(Context ctx, String dateVAL, String uuid) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        final String SOAP_ACTION = "http://tempuri.org/GetStoreProductClassificationTypeMaster";
        final String METHOD_NAME = "GetStoreProductClassificationTypeMaster";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("CityId", 1);

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            //// System.out.println("Aman a8 :"+responseBody);


            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }


            int chkProductClassificationContainsRow = 0;


            if (soDiffg.hasProperty("NewDataSet")) {

                chkProductClassificationContainsRow = 1;
                dbengine.deletetblStoreProductClassificationTypeListMstr();
            }


            if (chkProductClassificationContainsRow == 1) {
                int AutoIdStore = 0;
                if (table.getPropertyCount() > 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        // // System.out.println("table - prop count: "+ table.getPropertyCount());
                        tableRow = (SoapObject) table.getProperty(i);
                        //// System.out.println("i value: "+ i);

                        int CategoryNodeID = 0;
                        int CategoryNodeType = 0;
                        String Category = "NA";
                        int ProductTypeNodeID = 0;
                        int ProductTypeNodeType = 0;
                        String ProductType = "NA";

                        //CategoryNodeID,CategoryNodeType,Category,ProductTypeNodeID,ProductTypeNodeType,ProductType

                        if (tableRow.hasProperty("CategoryNodeID")) {
                            //// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("CategoryNodeID").toString().isEmpty()) {
                                CategoryNodeID = 0;
                            } else {
                                String abc = tableRow.getProperty("CategoryNodeID").toString().trim();
                                CategoryNodeID = Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("CategoryNodeType")) {
                            //// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("CategoryNodeType").toString().isEmpty()) {
                                CategoryNodeType = 0;
                            } else {
                                String abc = tableRow.getProperty("CategoryNodeType").toString().trim();
                                CategoryNodeType = Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("Category")) {
                            if (tableRow.getProperty("Category").toString().isEmpty()) {
                                Category = "NA";
                            } else {
                                Category = tableRow.getProperty("Category").toString().trim();
                            }
                        }

                        if (tableRow.hasProperty("ProductTypeNodeID")) {
                            //// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("ProductTypeNodeID").toString().isEmpty()) {
                                ProductTypeNodeID = 0;
                            } else {
                                String abc = tableRow.getProperty("ProductTypeNodeID").toString().trim();
                                ProductTypeNodeID = Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("ProductTypeNodeType")) {
                            //// System.out.println("Doctor id property exist :"+tableRow.hasProperty("DoctorsID"));
                            if (tableRow.getProperty("ProductTypeNodeType").toString().isEmpty()) {
                                ProductTypeNodeType = 0;
                            } else {
                                String abc = tableRow.getProperty("ProductTypeNodeType").toString().trim();
                                ProductTypeNodeType = Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("ProductType")) {
                            if (tableRow.getProperty("ProductType").toString().isEmpty()) {
                                ProductType = "NA";
                            } else {
                                ProductType = tableRow.getProperty("ProductType").toString().trim();
                            }
                        }


                        AutoIdStore = i + 1;
                        //// System.out.println("Arjun Golu StoreProductClassificationType Mstr Data from Server :"+"CategoryNodeID :"+CategoryNodeID+"Category :"+Category);


                        dbengine.savetblStoreProductClassificationTypeListMstr(AutoIdStore, CategoryNodeID, CategoryNodeType, Category, ProductTypeNodeID, ProductTypeNodeType, ProductType, 0, 0, "NA");

                    }
                }

            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 39;
            return setmovie;
//return counts;
        } catch (Exception e) {

            // System.out.println("Arjun getDistributorTypeMstr: 3 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }


    // Working for Parag Summary
    public ServiceWorker getCallspRptGetSKUWiseDaySummary(Context ctx, String uuid, String dateVAL) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        //String Sstat = "0";

        final String SOAP_ACTION = "http://tempuri.org/CallspRptGetSKUWiseDaySummary";
        final String METHOD_NAME = "CallspRptGetSKUWiseDaySummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service
        decimalFormat.applyPattern(pattern);
        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";
            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            // System.out.println("Summary new SKUWise occur value bydate"+ dateVAL.toString());
            // System.out.println("Summary new SKUWise occur value IMEINo"+ uuid.toString());


            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //////// System.out.println("Debug: " + "dbengine.open");

            //chkTblStoreListContainsRow
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //////// System.out.println("chkTblStoreListContainsRow: for StoreList "+ chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                int AutoId = 0;
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


                    String ProductId = "0";
                    String Product = "0";
                    String MRP = "0";
                    String Rate = "0";
                    String NoofStores = "0";
                    String OrderQty = "0";
                    String FreeQty = "0";
                    String DiscValue = "0";
                    String ValBeforeTax = "0";
                    String TaxValue = "0";
                    String ValAfterTax = "0";
                    String Lvl = "0";
                    String Category = "0";
                    String UOM = "0";

                    if (tableRow.hasProperty("ProductId")) {
                        if (tableRow.getProperty("ProductId").toString().isEmpty()) {
                            ProductId = "0";
                        } else {
                            ProductId = tableRow.getProperty("ProductId").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("Product")) {
                        if (tableRow.getProperty("Product").toString().isEmpty()) {
                            Product = "0";
                        } else {
                            Product = tableRow.getProperty("Product").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("MRP")) {
                        if (tableRow.getProperty("MRP").toString().isEmpty()) {
                            MRP = "0";
                        } else {
                            MRP = tableRow.getProperty("MRP").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("Rate")) {
                        if (tableRow.getProperty("Rate").toString().isEmpty()) {
                            Rate = "0";
                        } else {
                            Rate = tableRow.getProperty("Rate").toString().trim();
                        }
                    }


                    if (tableRow.hasProperty("NoofStores")) {
                        if (tableRow.getProperty("NoofStores").toString().isEmpty()) {
                            NoofStores = "0";
                        } else {
                            NoofStores = tableRow.getProperty("NoofStores").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("OrderQty")) {
                        if (tableRow.getProperty("OrderQty").toString().isEmpty()) {
                            OrderQty = "0";
                        } else {
                            OrderQty = tableRow.getProperty("OrderQty").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("FreeQty")) {
                        if (tableRow.getProperty("FreeQty").toString().isEmpty()) {
                            FreeQty = "0";
                        } else {
                            FreeQty = tableRow.getProperty("FreeQty").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("DiscValue")) {
                        if (tableRow.getProperty("DiscValue").toString().isEmpty()) {
                            DiscValue = "0";
                        } else {
                            DiscValue = tableRow.getProperty("DiscValue").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("ValBeforeTax")) {
                        if (tableRow.getProperty("ValBeforeTax").toString().isEmpty()) {
                            ValBeforeTax = "0";
                        } else {
                            ValBeforeTax = tableRow.getProperty("ValBeforeTax").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("TaxValue")) {
                        if (tableRow.getProperty("TaxValue").toString().isEmpty()) {
                            TaxValue = "0";
                        } else {
                            TaxValue = tableRow.getProperty("TaxValue").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("ValAfterTax")) {
                        if (tableRow.getProperty("ValAfterTax").toString().isEmpty()) {
                            ValAfterTax = "0";
                        } else {
                            ValAfterTax = tableRow.getProperty("ValAfterTax").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("Lvl")) {
                        if (tableRow.getProperty("Lvl").toString().isEmpty()) {
                            Lvl = "0";
                        } else {
                            Lvl = tableRow.getProperty("Lvl").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("Category")) {
                        if (tableRow.getProperty("Category").toString().isEmpty()) {
                            Category = "0";
                        } else {
                            Category = tableRow.getProperty("Category").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("UOM")) {
                        if (tableRow.getProperty("UOM").toString().isEmpty()) {
                            UOM = "0";
                        } else {
                            UOM = tableRow.getProperty("UOM").toString().trim();

                        }
                    }


                    AutoId = i + 1;

                    // System.out.println("Value inserting  231 AutoId:"+AutoId);
                    // System.out.println("Value inserting  231 ProductId:"+ProductId);
                    // System.out.println("Value inserting  231 Product:"+Product);
                    // System.out.println("Value inserting  231 MRP:"+MRP);
                    // System.out.println("Value inserting  231 Rate:"+Rate);
                    // System.out.println("Value inserting  231 NoofStores:"+NoofStores);
                    // System.out.println("Value inserting  231 OrderQty:"+OrderQty);
                    // System.out.println("Value inserting  231 FreeQty:"+FreeQty);

                    dbengine.savetblSKUWiseDaySummary(AutoId, ProductId, Product, MRP,
                            Rate, NoofStores, OrderQty, FreeQty, DiscValue,
                            ValBeforeTax, TaxValue, ValAfterTax, Lvl, Category, UOM);


                }
            }

            dbengine.close();        // #4

				/*setmovie.director = tableRow.getProperty("Director").toString();
				setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";


            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in CallspRptGetSKUWiseDaySummary :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }


    }

    public ServiceWorker getCallspRptGetStoreWiseDaySummary(Context ctx, String uuid, String dateVAL) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        //String Sstat = "0";

        final String SOAP_ACTION = "http://tempuri.org/CallspRptGetStoreWiseDaySummary";
        final String METHOD_NAME = "CallspRptGetStoreWiseDaySummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service
        decimalFormat.applyPattern(pattern);
        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";
            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            // System.out.println("Summary new SKUWise occur value bydate"+ dateVAL.toString());
            // System.out.println("Summary new SKUWise occur value IMEINo"+ uuid.toString());


            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //////// System.out.println("Debug: " + "dbengine.open");

            //chkTblStoreListContainsRow
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //////// System.out.println("chkTblStoreListContainsRow: for StoreList "+ chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                int AutoId = 0;
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


                    String Store = "0";
                    String LinesperBill = "0";
                    String StockValue = "0";
                    String DiscValue = "0";
                    String ValBeforeTax = "0";
                    String TaxValue = "0";
                    String ValAfterTax = "0";
                    String Lvl = "0";
                    String StockQty = "0";

                    if (tableRow.hasProperty("Store")) {
                        if (tableRow.getProperty("Store").toString().isEmpty()) {
                            Store = "0";
                        } else {
                            Store = tableRow.getProperty("Store").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("LinesperBill")) {
                        if (tableRow.getProperty("LinesperBill").toString().isEmpty()) {
                            LinesperBill = "0";
                        } else {
                            LinesperBill = tableRow.getProperty("LinesperBill").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("StockValue")) {
                        if (tableRow.getProperty("StockValue").toString().isEmpty()) {
                            StockValue = "0";
                        } else {
                            StockValue = tableRow.getProperty("StockValue").toString().trim();
                        }
                    }


                    if (tableRow.hasProperty("DiscValue")) {
                        if (tableRow.getProperty("DiscValue").toString().isEmpty()) {
                            DiscValue = "0";
                        } else {
                            DiscValue = tableRow.getProperty("DiscValue").toString().trim();
                        }
                    }


                    if (tableRow.hasProperty("ValBeforeTax")) {
                        if (tableRow.getProperty("ValBeforeTax").toString().isEmpty()) {
                            ValBeforeTax = "0";
                        } else {
                            ValBeforeTax = tableRow.getProperty("ValBeforeTax").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("TaxValue")) {
                        if (tableRow.getProperty("TaxValue").toString().isEmpty()) {
                            TaxValue = "0";
                        } else {
                            TaxValue = tableRow.getProperty("TaxValue").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("ValAfterTax")) {
                        if (tableRow.getProperty("ValAfterTax").toString().isEmpty()) {
                            ValAfterTax = "0";
                        } else {
                            ValAfterTax = tableRow.getProperty("ValAfterTax").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("Lvl")) {
                        if (tableRow.getProperty("Lvl").toString().isEmpty()) {
                            Lvl = "0";
                        } else {
                            Lvl = tableRow.getProperty("Lvl").toString().trim();

                        }
                    }


                    AutoId = i + 1;


                    dbengine.savetblStoreWiseDaySummary(AutoId, Store, LinesperBill, StockValue,
                            DiscValue, ValBeforeTax, TaxValue, ValAfterTax, Lvl);


                }
            }

            dbengine.close();        // #4

				/*setmovie.director = tableRow.getProperty("Director").toString();
				setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";


            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetStoreListMR :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }


    }

    public ServiceWorker getCallspRptGetStoreSKUWiseDaySummary(Context ctx, String uuid, String dateVAL) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        //String Sstat = "0";

        final String SOAP_ACTION = "http://tempuri.org/CallspRptGetStoreSKUWiseDaySummary";
        final String METHOD_NAME = "CallspRptGetStoreSKUWiseDaySummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service
        decimalFormat.applyPattern(pattern);
        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";
            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            // System.out.println("Summary new SKUWise occur value bydate"+ dateVAL.toString());
            // System.out.println("Summary new SKUWise occur value IMEINo"+ uuid.toString());


            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //////// System.out.println("Debug: " + "dbengine.open");

            //chkTblStoreListContainsRow
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //////// System.out.println("chkTblStoreListContainsRow: for StoreList "+ chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                int AutoId = 0;
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


                    String ProductId = "0";
                    String Product = "0";
                    String MRP = "0";
                    String Rate = "0";
                    String OrderQty = "0";
                    String FreeQty = "0";
                    String DiscValue = "0";
                    String ValBeforeTax = "0";
                    String TaxValue = "0";
                    String ValAfterTax = "0";
                    String Lvl = "0";
                    String StoreId = "0";
                    String StockQty = "0";


                    if (tableRow.hasProperty("ProductId")) {
                        if (tableRow.getProperty("ProductId").toString().isEmpty()) {
                            ProductId = "0";
                        } else {
                            ProductId = tableRow.getProperty("ProductId").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("Product")) {
                        if (tableRow.getProperty("Product").toString().isEmpty()) {
                            Product = "0";
                        } else {
                            Product = tableRow.getProperty("Product").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("MRP")) {
                        if (tableRow.getProperty("MRP").toString().isEmpty()) {
                            MRP = "0";
                        } else {
                            MRP = tableRow.getProperty("MRP").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("Rate")) {
                        if (tableRow.getProperty("Rate").toString().isEmpty()) {
                            Rate = "0";
                        } else {
                            Rate = tableRow.getProperty("Rate").toString().trim();
                        }
                    }


                    if (tableRow.hasProperty("OrderQty")) {
                        if (tableRow.getProperty("OrderQty").toString().isEmpty()) {
                            OrderQty = "0";
                        } else {
                            OrderQty = tableRow.getProperty("OrderQty").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("FreeQty")) {
                        if (tableRow.getProperty("FreeQty").toString().isEmpty()) {
                            FreeQty = "0";
                        } else {
                            FreeQty = tableRow.getProperty("FreeQty").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("DiscValue")) {
                        if (tableRow.getProperty("DiscValue").toString().isEmpty()) {
                            DiscValue = "0";
                        } else {
                            DiscValue = tableRow.getProperty("DiscValue").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("ValBeforeTax")) {
                        if (tableRow.getProperty("ValBeforeTax").toString().isEmpty()) {
                            ValBeforeTax = "0";
                        } else {
                            ValBeforeTax = tableRow.getProperty("ValBeforeTax").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("TaxValue")) {
                        if (tableRow.getProperty("TaxValue").toString().isEmpty()) {
                            TaxValue = "0";
                        } else {
                            TaxValue = tableRow.getProperty("TaxValue").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("ValAfterTax")) {
                        if (tableRow.getProperty("ValAfterTax").toString().isEmpty()) {
                            ValAfterTax = "0";
                        } else {
                            ValAfterTax = tableRow.getProperty("ValAfterTax").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("Lvl")) {
                        if (tableRow.getProperty("Lvl").toString().isEmpty()) {
                            Lvl = "0";
                        } else {
                            Lvl = tableRow.getProperty("Lvl").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("StoreId")) {
                        if (tableRow.getProperty("StoreId").toString().isEmpty()) {
                            StoreId = "0";
                        } else {
                            StoreId = tableRow.getProperty("StoreId").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("StockQty")) {
                        if (tableRow.getProperty("StockQty").toString().isEmpty()) {
                            StockQty = "0";
                        } else {
                            StockQty = tableRow.getProperty("StockQty").toString().trim();

                        }
                    }


                    AutoId = i + 1;


                    dbengine.savetblStoreSKUWiseDaySummary(AutoId, ProductId, Product, MRP,
                            Rate, OrderQty, FreeQty, DiscValue,
                            ValBeforeTax, TaxValue, ValAfterTax, Lvl, StoreId, StockQty);


                }
            }

            dbengine.close();        // #4

				/*setmovie.director = tableRow.getProperty("Director").toString();
				setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";


            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetStoreListMR :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }


    }

    public ServiceWorker getCallspPDAGetDaySummary(Context ctx, String uuid, String dateVAL) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        //String Sstat = "0";

        final String SOAP_ACTION = "http://tempuri.org/CallspPDAGetDaySummary";
        final String METHOD_NAME = "CallspPDAGetDaySummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service
        decimalFormat.applyPattern(pattern);
        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";
            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            // System.out.println("Summary new SKUWise occur value bydate"+ dateVAL.toString());
            // System.out.println("Summary new SKUWise occur value IMEINo"+ uuid.toString());


            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //////// System.out.println("Debug: " + "dbengine.open");

            //chkTblStoreListContainsRow
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //////// System.out.println("chkTblStoreListContainsRow: for StoreList "+ chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                int AutoId = 0;
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


                    String Measures = "0";
                    String TodaysSummary = "0";
                    String MTDSummary = "0";


                    if (tableRow.hasProperty("Measures")) {
                        if (tableRow.getProperty("Measures").toString().isEmpty()) {
                            Measures = "0";
                        } else {
                            Measures = tableRow.getProperty("Measures").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("TodaysSummary")) {
                        if (tableRow.getProperty("TodaysSummary").toString().isEmpty()) {
                            TodaysSummary = "0";
                        } else {
                            TodaysSummary = tableRow.getProperty("TodaysSummary").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("MTDSummary")) {
                        if (tableRow.getProperty("MTDSummary").toString().isEmpty()) {
                            MTDSummary = "0";
                        } else {
                            MTDSummary = tableRow.getProperty("MTDSummary").toString().trim();
                        }
                    }


                    AutoId = i + 1;


                    dbengine.savetblAllSummary(AutoId, Measures, TodaysSummary, MTDSummary);


                }
            }

            dbengine.close();        // #4

				/*setmovie.director = tableRow.getProperty("Director").toString();
				setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";


            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetStoreListMR :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }


    }


    public ServiceWorker getCallspRptGetSKUWiseMTDSummary(Context ctx, String uuid, String dateVAL) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        //String Sstat = "0";

        final String SOAP_ACTION = "http://tempuri.org/CallspRptGetSKUWiseMTDSummary";
        final String METHOD_NAME = "CallspRptGetSKUWiseMTDSummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service
        decimalFormat.applyPattern(pattern);
        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";
            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            // System.out.println("Summary new SKUWise occur value bydate"+ dateVAL.toString());
            // System.out.println("Summary new SKUWise occur value IMEINo"+ uuid.toString());


            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //////// System.out.println("Debug: " + "dbengine.open");

            //chkTblStoreListContainsRow
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //////// System.out.println("chkTblStoreListContainsRow: for StoreList "+ chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                int AutoId = 0;
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


                    String ProductId = "0";
                    String Product = "0";
                    String MRP = "0";
                    String Rate = "0";
                    String NoofStores = "0";
                    String OrderQty = "0";
                    String FreeQty = "0";
                    String DiscValue = "0";
                    String ValBeforeTax = "0";
                    String TaxValue = "0";
                    String ValAfterTax = "0";
                    String Lvl = "0";
                    String Category = "0";
                    String UOM = "0";

                    if (tableRow.hasProperty("ProductId")) {
                        if (tableRow.getProperty("ProductId").toString().isEmpty()) {
                            ProductId = "0";
                        } else {
                            ProductId = tableRow.getProperty("ProductId").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("Product")) {
                        if (tableRow.getProperty("Product").toString().isEmpty()) {
                            Product = "0";
                        } else {
                            Product = tableRow.getProperty("Product").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("MRP")) {
                        if (tableRow.getProperty("MRP").toString().isEmpty()) {
                            MRP = "0";
                        } else {
                            MRP = tableRow.getProperty("MRP").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("Rate")) {
                        if (tableRow.getProperty("Rate").toString().isEmpty()) {
                            Rate = "0";
                        } else {
                            Rate = tableRow.getProperty("Rate").toString().trim();
                        }
                    }


                    if (tableRow.hasProperty("NoofStores")) {
                        if (tableRow.getProperty("NoofStores").toString().isEmpty()) {
                            NoofStores = "0";
                        } else {
                            NoofStores = tableRow.getProperty("NoofStores").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("OrderQty")) {
                        if (tableRow.getProperty("OrderQty").toString().isEmpty()) {
                            OrderQty = "0";
                        } else {
                            OrderQty = tableRow.getProperty("OrderQty").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("FreeQty")) {
                        if (tableRow.getProperty("FreeQty").toString().isEmpty()) {
                            FreeQty = "0";
                        } else {
                            FreeQty = tableRow.getProperty("FreeQty").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("DiscValue")) {
                        if (tableRow.getProperty("DiscValue").toString().isEmpty()) {
                            DiscValue = "0";
                        } else {
                            DiscValue = tableRow.getProperty("DiscValue").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("ValBeforeTax")) {
                        if (tableRow.getProperty("ValBeforeTax").toString().isEmpty()) {
                            ValBeforeTax = "0";
                        } else {
                            ValBeforeTax = tableRow.getProperty("ValBeforeTax").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("TaxValue")) {
                        if (tableRow.getProperty("TaxValue").toString().isEmpty()) {
                            TaxValue = "0";
                        } else {
                            TaxValue = tableRow.getProperty("TaxValue").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("ValAfterTax")) {
                        if (tableRow.getProperty("ValAfterTax").toString().isEmpty()) {
                            ValAfterTax = "0";
                        } else {
                            ValAfterTax = tableRow.getProperty("ValAfterTax").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("Lvl")) {
                        if (tableRow.getProperty("Lvl").toString().isEmpty()) {
                            Lvl = "0";
                        } else {
                            Lvl = tableRow.getProperty("Lvl").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("Category")) {
                        if (tableRow.getProperty("Category").toString().isEmpty()) {
                            Category = "0";
                        } else {
                            Category = tableRow.getProperty("Category").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("UOM")) {
                        if (tableRow.getProperty("UOM").toString().isEmpty()) {
                            UOM = "0";
                        } else {
                            UOM = tableRow.getProperty("UOM").toString().trim();

                        }
                    }


                    AutoId = i + 1;

                    // System.out.println("Value inserting  231 AutoId:"+AutoId);
                    // System.out.println("Value inserting  231 ProductId:"+ProductId);
                    // System.out.println("Value inserting  231 Product:"+Product);
                    // System.out.println("Value inserting  231 MRP:"+MRP);
                    // System.out.println("Value inserting  231 Rate:"+Rate);
                    // System.out.println("Value inserting  231 NoofStores:"+NoofStores);
                    // System.out.println("Value inserting  231 OrderQty:"+OrderQty);
                    // System.out.println("Value inserting  231 FreeQty:"+FreeQty);

                    dbengine.savetblSKUWiseDaySummary(AutoId, ProductId, Product, MRP,
                            Rate, NoofStores, OrderQty, FreeQty, DiscValue,
                            ValBeforeTax, TaxValue, ValAfterTax, Lvl, Category, UOM);


                }
            }

            dbengine.close();        // #4

				/*setmovie.director = tableRow.getProperty("Director").toString();
				setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";


            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in CallspRptGetSKUWiseDaySummary :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }


    }

    public ServiceWorker getCallspRptGetStoreWiseMTDSummary(Context ctx, String uuid, String dateVAL) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        //String Sstat = "0";

        final String SOAP_ACTION = "http://tempuri.org/CallspRptGetStoreWiseMTDSummary";
        final String METHOD_NAME = "CallspRptGetStoreWiseMTDSummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service
        decimalFormat.applyPattern(pattern);
        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";
            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            // System.out.println("Summary new SKUWise occur value bydate"+ dateVAL.toString());
            // System.out.println("Summary new SKUWise occur value IMEINo"+ uuid.toString());


            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //////// System.out.println("Debug: " + "dbengine.open");

            //chkTblStoreListContainsRow
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //////// System.out.println("chkTblStoreListContainsRow: for StoreList "+ chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                int AutoId = 0;
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


                    String Store = "0";
                    String LinesperBill = "0";
                    String StockValue = "0";
                    String DiscValue = "0";
                    String ValBeforeTax = "0";
                    String TaxValue = "0";
                    String ValAfterTax = "0";
                    String Lvl = "0";

                    if (tableRow.hasProperty("Store")) {
                        if (tableRow.getProperty("Store").toString().isEmpty()) {
                            Store = "0";
                        } else {
                            Store = tableRow.getProperty("Store").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("LinesperBill")) {
                        if (tableRow.getProperty("LinesperBill").toString().isEmpty()) {
                            LinesperBill = "0";
                        } else {
                            LinesperBill = tableRow.getProperty("LinesperBill").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("StockValue")) {
                        if (tableRow.getProperty("StockValue").toString().isEmpty()) {
                            StockValue = "0";
                        } else {
                            StockValue = tableRow.getProperty("StockValue").toString().trim();
                        }
                    }


                    if (tableRow.hasProperty("DiscValue")) {
                        if (tableRow.getProperty("DiscValue").toString().isEmpty()) {
                            DiscValue = "0";
                        } else {
                            DiscValue = tableRow.getProperty("DiscValue").toString().trim();
                        }
                    }


                    if (tableRow.hasProperty("ValBeforeTax")) {
                        if (tableRow.getProperty("ValBeforeTax").toString().isEmpty()) {
                            ValBeforeTax = "0";
                        } else {
                            ValBeforeTax = tableRow.getProperty("ValBeforeTax").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("TaxValue")) {
                        if (tableRow.getProperty("TaxValue").toString().isEmpty()) {
                            TaxValue = "0";
                        } else {
                            TaxValue = tableRow.getProperty("TaxValue").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("ValAfterTax")) {
                        if (tableRow.getProperty("ValAfterTax").toString().isEmpty()) {
                            ValAfterTax = "0";
                        } else {
                            ValAfterTax = tableRow.getProperty("ValAfterTax").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("Lvl")) {
                        if (tableRow.getProperty("Lvl").toString().isEmpty()) {
                            Lvl = "0";
                        } else {
                            Lvl = tableRow.getProperty("Lvl").toString().trim();

                        }
                    }


                    AutoId = i + 1;


                    dbengine.savetblStoreWiseDaySummary(AutoId, Store, LinesperBill, StockValue,
                            DiscValue, ValBeforeTax, TaxValue, ValAfterTax, Lvl);


                }
            }

            dbengine.close();        // #4

				/*setmovie.director = tableRow.getProperty("Director").toString();
				setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";


            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetStoreListMR :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }


    }

    public ServiceWorker getCallspRptGetStoreSKUWiseMTDSummary(Context ctx, String uuid, String dateVAL) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        //String Sstat = "0";

        final String SOAP_ACTION = "http://tempuri.org/CallspRptGetStoreSKUWiseMTDSummary";
        final String METHOD_NAME = "CallspRptGetStoreSKUWiseMTDSummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service
        decimalFormat.applyPattern(pattern);
        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";
            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            // System.out.println("Summary new SKUWise occur value bydate"+ dateVAL.toString());
            // System.out.println("Summary new SKUWise occur value IMEINo"+ uuid.toString());


            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //////// System.out.println("Debug: " + "dbengine.open");

            //chkTblStoreListContainsRow
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //////// System.out.println("chkTblStoreListContainsRow: for StoreList "+ chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                int AutoId = 0;
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


                    String ProductId = "0";
                    String Product = "0";
                    String MRP = "0";
                    String Rate = "0";
                    String OrderQty = "0";
                    String FreeQty = "0";
                    String DiscValue = "0";
                    String ValBeforeTax = "0";
                    String TaxValue = "0";
                    String ValAfterTax = "0";
                    String Lvl = "0";
                    String StoreId = "0";
                    String StockQty = "0";


                    if (tableRow.hasProperty("ProductId")) {
                        if (tableRow.getProperty("ProductId").toString().isEmpty()) {
                            ProductId = "0";
                        } else {
                            ProductId = tableRow.getProperty("ProductId").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("Product")) {
                        if (tableRow.getProperty("Product").toString().isEmpty()) {
                            Product = "0";
                        } else {
                            Product = tableRow.getProperty("Product").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("MRP")) {
                        if (tableRow.getProperty("MRP").toString().isEmpty()) {
                            MRP = "0";
                        } else {
                            MRP = tableRow.getProperty("MRP").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("Rate")) {
                        if (tableRow.getProperty("Rate").toString().isEmpty()) {
                            Rate = "0";
                        } else {
                            Rate = tableRow.getProperty("Rate").toString().trim();
                        }
                    }


                    if (tableRow.hasProperty("OrderQty")) {
                        if (tableRow.getProperty("OrderQty").toString().isEmpty()) {
                            OrderQty = "0";
                        } else {
                            OrderQty = tableRow.getProperty("OrderQty").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("FreeQty")) {
                        if (tableRow.getProperty("FreeQty").toString().isEmpty()) {
                            FreeQty = "0";
                        } else {
                            FreeQty = tableRow.getProperty("FreeQty").toString().trim();

                        }
                    }
                    if (tableRow.hasProperty("DiscValue")) {
                        if (tableRow.getProperty("DiscValue").toString().isEmpty()) {
                            DiscValue = "0";
                        } else {
                            DiscValue = tableRow.getProperty("DiscValue").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("ValBeforeTax")) {
                        if (tableRow.getProperty("ValBeforeTax").toString().isEmpty()) {
                            ValBeforeTax = "0";
                        } else {
                            ValBeforeTax = tableRow.getProperty("ValBeforeTax").toString().trim();

                        }
                    }


                    if (tableRow.hasProperty("TaxValue")) {
                        if (tableRow.getProperty("TaxValue").toString().isEmpty()) {
                            TaxValue = "0";
                        } else {
                            TaxValue = tableRow.getProperty("TaxValue").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("ValAfterTax")) {
                        if (tableRow.getProperty("ValAfterTax").toString().isEmpty()) {
                            ValAfterTax = "0";
                        } else {
                            ValAfterTax = tableRow.getProperty("ValAfterTax").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("Lvl")) {
                        if (tableRow.getProperty("Lvl").toString().isEmpty()) {
                            Lvl = "0";
                        } else {
                            Lvl = tableRow.getProperty("Lvl").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("StoreId")) {
                        if (tableRow.getProperty("StoreId").toString().isEmpty()) {
                            StoreId = "0";
                        } else {
                            StoreId = tableRow.getProperty("StoreId").toString().trim();

                        }
                    }

                    if (tableRow.hasProperty("StockQty")) {
                        if (tableRow.getProperty("StockQty").toString().isEmpty()) {
                            StockQty = "0";
                        } else {
                            StockQty = tableRow.getProperty("StockQty").toString().trim();

                        }
                    }


                    AutoId = i + 1;


                    dbengine.savetblStoreSKUWiseDaySummary(AutoId, ProductId, Product, MRP,
                            Rate, OrderQty, FreeQty, DiscValue,
                            ValBeforeTax, TaxValue, ValAfterTax, Lvl, StoreId, StockQty);


                }
            }

            dbengine.close();        // #4

				/*setmovie.director = tableRow.getProperty("Director").toString();
				setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";


            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetStoreListMR :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }


    }


    public ServiceWorker getNewStoreInfoDynamic(Context ctx, JSONArray ArrJSONTable, JSONArray ArrOutletGeneralInfoTable, String imei, JSONArray ArrOutletPaymentDetails) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);

        String RouteType = "0";
        String RouteID = "0";
        try {
            dbengine.open();
            RouteID = dbengine.GetActiveRouteID();
            RouteType = dbengine.FetchRouteType(RouteID);
            dbengine.close();
            System.out.println("hi" + RouteType);
        } catch (Exception e) {
            System.out.println("error" + e);
        }
        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnAddNewStoreJason";
        final String METHOD_NAME = "fnAddNewStoreJason";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        //Create request
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            SimpleDateFormat currDateFormatFroEntryJasonData = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();
            String DateTimeParameter = currDateFormatFroEntryJasonData.format(currDate).toString();


				/*JSONArray ArrAbhinavTable = new JSONArray();
				 JSONObject post_dict = new JSONObject();
				   try
				   {
					   for(int i=0;i<3;i++)
					   {
				             post_dict.put("QuestionId" , "1");
				             post_dict.put("AnsCtronlType", "2");
				             post_dict.put("Value", "3");
				             ArrAbhinavTable.put(post_dict);
					   }
				         }
				   catch (JSONException e)
				     {
				             e.printStackTrace();
				         }
				*/


            int DatabaseVersion = CommonInfo.DATABASE_VERSIONID;
            int ApplicationID = CommonInfo.Application_TypeID;

            client.addProperty("NewStoreGeneralJasonData", ArrOutletGeneralInfoTable.toString().trim());
            client.addProperty("NewStoreJasonData", ArrJSONTable.toString().trim());
            client.addProperty("uuid", imei);
            client.addProperty("DatabaseVersion", DatabaseVersion);
            client.addProperty("ApplicationID", ApplicationID);
            client.addProperty("rID", RouteID.toString());
            client.addProperty("RouteType", RouteType);
            client.addProperty("DateTimeParameter", DateTimeParameter);
            client.addProperty("NewStoreOutletPaymentDetails", ArrOutletPaymentDetails.toString().trim());


            dbengine.open();


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            String resultString = androidHttpTransport.responseDump;
            // remove information XML,only retrieved results that returned

            //	responseBody = (SoapObject) responseBody.getProperty(1);

            //int count123=responseBody.getPropertyCount();
            // get information XMl of tables that is returned
            String name = responseBody.getProperty(0).toString();

            // System.out.println("Getting Response by Shivam :"+name);


            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);




		           /* <NewStoreAllInfo>
		            <ID>70</ID>
		            <StoreID>70</StoreID>
		            <StoreName>sunil test3 -fbd</StoreName>
		            <StoreLatitude>28.380205830000000000000000</StoreLatitude>
		            <StoreLongitude>77.277337660000000000000000</StoreLongitude>
		            <LastTransactionDate>Mar 4 201</LastTransactionDate>
		            <LastVisitDate>Mar 4 201</LastVisitDate>
		            <Sstat>0</Sstat>
		             <IsClose>0</IsClose>
		            <IsNextDat>0</IsNextDat>
		            <RouteID>9</RouteID>
		            </NewStoreAllInfo>*/


		           /* <NewStoreIDInfo>
		            <StoreID>70</StoreID>
		            <StoreIDKey>619932-ab7adc09</StoreIDKey>
		            </NewStoreIDInfo> */

            NodeList tblNewStoreAllInfoNode = doc.getElementsByTagName("NewStoreAllInfo");
            for (int i = 0; i < tblNewStoreAllInfoNode.getLength(); i++) {
                String StoreID = "0";
                String StoreName = "NA";
                Double StoreLatitude = 0.0;
                Double StoreLongitude = 0.0;
                String StoreType = "0";
                String LastTransactionDate = "NA";
                String LastVisitDate = "NA";
                int Sstat = 0;
                int IsClose = 0;
                int IsNextDat = 0;
                int RouteIDNew = 0;
                int flgHasQuote = 0;
                int flgAllowQuotation = 1;
                int flgSubmitFromQuotation = 0;

                String flgGSTCapture = "1";
                String flgGSTCompliance = "0";
                String GSTNumber = "NA";
                int flgGSTRecordFromServer = 0;

                //Payment Satge Missing


                Element element = (Element) tblNewStoreAllInfoNode.item(i);

                NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                Element line = (Element) StoreIDNode.item(0);
                StoreID = xmlParser.getCharacterDataFromElement(line);

                NodeList StoreNameNode = element.getElementsByTagName("StoreName");
                line = (Element) StoreNameNode.item(0);
                StoreName = xmlParser.getCharacterDataFromElement(line);

                NodeList StoreLatitudeNode = element.getElementsByTagName("StoreLatitude");
                line = (Element) StoreLatitudeNode.item(0);
                StoreLatitude = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));

                NodeList StoreLongitudeNode = element.getElementsByTagName("StoreLongitude");
                line = (Element) StoreLongitudeNode.item(0);
                StoreLongitude = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));

                NodeList StoreTypeNode = element.getElementsByTagName("StoreType");
                line = (Element) StoreTypeNode.item(0);
                StoreType = xmlParser.getCharacterDataFromElement(line);


                NodeList LastTransactionDateNode = element.getElementsByTagName("LastTransactionDate");
                line = (Element) LastTransactionDateNode.item(0);
                LastTransactionDate = xmlParser.getCharacterDataFromElement(line);

                NodeList LastVisitDateNode = element.getElementsByTagName("LastVisitDate");
                line = (Element) LastVisitDateNode.item(0);
                LastVisitDate = xmlParser.getCharacterDataFromElement(line);

                NodeList SstatNode = element.getElementsByTagName("Sstat");
                line = (Element) SstatNode.item(0);
                Sstat = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                NodeList IsCloseNode = element.getElementsByTagName("IsClose");
                line = (Element) IsCloseNode.item(0);
                IsClose = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                NodeList IsNextDatNode = element.getElementsByTagName("IsNextDat");
                line = (Element) IsNextDatNode.item(0);
                IsNextDat = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                NodeList RouteIDNode = element.getElementsByTagName("RouteID");
                line = (Element) RouteIDNode.item(0);
                RouteIDNew = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));


                NodeList flgHasQuoteNode = element.getElementsByTagName("flgHasQuote");
                line = (Element) flgHasQuoteNode.item(0);
                flgHasQuote = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));


                NodeList flgAllowQuotationNode = element.getElementsByTagName("flgAllowQuotation");
                line = (Element) flgAllowQuotationNode.item(0);
                flgAllowQuotation = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                NodeList flgSubmitFromQuotationNode = element.getElementsByTagName("flgSubmitFromQuotation");
                line = (Element) flgSubmitFromQuotationNode.item(0);
                flgSubmitFromQuotation = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                if (!element.getElementsByTagName("flgGSTCapture").equals(null)) {
                    NodeList flgGSTCaptureNode = element.getElementsByTagName("flgGSTCapture");
                    line = (Element) flgGSTCaptureNode.item(0);
                    if (flgGSTCaptureNode.getLength() > 0) {
                        flgGSTCapture = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("flgGSTCompliance").equals(null)) {
                    NodeList flgGSTComplianceNode = element.getElementsByTagName("flgGSTCompliance");
                    line = (Element) flgGSTComplianceNode.item(0);
                    if (flgGSTComplianceNode.getLength() > 0) {
                        flgGSTCompliance = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("GSTNumber").equals(null)) {
                    NodeList GSTNumberNode = element.getElementsByTagName("GSTNumber");
                    line = (Element) GSTNumberNode.item(0);
                    if (GSTNumberNode.getLength() > 0) {
                        GSTNumber = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (flgGSTCompliance.equals("1")) {
                    flgGSTRecordFromServer = 1;
                }
                if (flgGSTCapture.equals(null)) {
                    flgGSTCapture = "1";
                }
                if (flgGSTCompliance.equals(null)) {
                    flgGSTCompliance = "NA";
                }
                if (GSTNumber.equals(null)) {
                    GSTNumber = "NA";
                }
                //flgSubmitFromQuotation
                int AutoIdStore = 0;
                ;
                AutoIdStore = dbengine.checkStoreListTableCount();

                AutoIdStore = AutoIdStore + 1;


                long syncTIMESTAMP = System.currentTimeMillis();
                Date dateobj = new Date(syncTIMESTAMP);
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                String dateVAL = df.format(dateobj);

                //	AddNewStore_DynamicSectionWise.ServiceWorkerDataComingFlag=1;
                //	AddNewStore_DynamicSectionWise.ServiceWorkerStoreID=StoreID;
                //	AddNewStore_DynamicSectionWise.ServiceWorkerResultSet=StoreName+"^"+StoreType+"^"+StoreLatitude+"^"+StoreLongitude
                //	+"^"+LastVisitDate+"^"+LastTransactionDate+"^"+dateVAL.toString().trim()+"^"+AutoIdStore+"^"+Sstat+"^"+IsClose+"^"+IsNextDat+"^"+RouteIDNew+"^"+flgHasQuote+"^"+flgAllowQuotation+"^"+flgSubmitFromQuotation+"^"+flgGSTCapture+"^"+flgGSTCompliance+"^"+GSTNumber+"^"+flgGSTRecordFromServer;


            }


            NodeList tblStoreListWithPaymentAddressMR = doc.getElementsByTagName("tblStoreListWithPaymentAddressMR");
            for (int i = 0; i < tblStoreListWithPaymentAddressMR.getLength(); i++) {

                String StoreID = "0";
                int OutAddTypeID = 0;

                String Address = "";
                String AddressDet = "Not Available";
                int OutAddID = 0;


                Element element = (Element) tblStoreListWithPaymentAddressMR.item(i);

                if (!element.getElementsByTagName("StoreID").equals(null)) {

                    NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                    Element line = (Element) StoreIDNode.item(0);

                    if (StoreIDNode.getLength() > 0) {

                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("OutAddTypeID").equals(null)) {

                    NodeList OutAddTypeIDNode = element.getElementsByTagName("OutAddTypeID");
                    Element line = (Element) OutAddTypeIDNode.item(0);

                    if (OutAddTypeIDNode.getLength() > 0) {

                        OutAddTypeID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("Address").equals(null)) {

                    NodeList AddressNode = element.getElementsByTagName("Address");
                    Element line = (Element) AddressNode.item(0);
                    if (AddressNode.getLength() > 0) {
                        Address = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("AddressDet").equals(null)) {
                    NodeList AddressDetNode = element.getElementsByTagName("AddressDet");
                    Element line = (Element) AddressDetNode.item(0);
                    if (AddressDetNode.getLength() > 0) {
                        AddressDet = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("OutAddID").equals(null)) {
                    NodeList OutAddIDNode = element.getElementsByTagName("OutAddID");
                    Element line = (Element) OutAddIDNode.item(0);
                    if (OutAddIDNode.getLength() > 0) {
                        OutAddID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                int AutoIdStore = 0;
                AutoIdStore = i + 1;

                dbengine.saveSOAPdataStoreListAddressMap(StoreID, OutAddTypeID, Address, AddressDet, OutAddID);
            }

            NodeList tblStoreSomeProdQuotePriceMstr = doc.getElementsByTagName("tblStoreSomeProdQuotePriceMstr");
            for (int i = 0; i < tblStoreSomeProdQuotePriceMstr.getLength(); i++) {

                String prdId = "0";
                String StoreID = "0";
                String QPBT = "0";
                String QPAT = "0";
                String QPTaxAmt = "0";
                int MinDlvryQty = 0;
                String UOMID = "0";


                Element element = (Element) tblStoreSomeProdQuotePriceMstr.item(i);

                if (!element.getElementsByTagName("PrdId").equals(null)) {

                    NodeList PrdIdNode = element.getElementsByTagName("PrdId");
                    Element line = (Element) PrdIdNode.item(0);

                    if (PrdIdNode.getLength() > 0) {

                        prdId = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("StoreId").equals(null)) {

                    NodeList StoreIDNode = element.getElementsByTagName("StoreId");
                    Element line = (Element) StoreIDNode.item(0);

                    if (StoreIDNode.getLength() > 0) {

                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("QPBT").equals(null)) {

                    NodeList QPBTNode = element.getElementsByTagName("QPBT");
                    Element line = (Element) QPBTNode.item(0);

                    if (QPBTNode.getLength() > 0) {

                        QPBT = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("QPAT").equals(null)) {

                    NodeList QPATNode = element.getElementsByTagName("QPAT");
                    Element line = (Element) QPATNode.item(0);

                    if (QPATNode.getLength() > 0) {

                        QPAT = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("QPTaxAmt").equals(null)) {

                    NodeList QPTaxmtNode = element.getElementsByTagName("QPTaxAmt");
                    Element line = (Element) QPTaxmtNode.item(0);

                    if (QPTaxmtNode.getLength() > 0) {

                        QPTaxAmt = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("MinDlvryQty").equals(null)) {

                    NodeList AddressDetNode = element.getElementsByTagName("MinDlvryQty");
                    Element line = (Element) AddressDetNode.item(0);

                    if (AddressDetNode.getLength() > 0) {

                        MinDlvryQty = Integer.valueOf(xmlParser.getCharacterDataFromElement(line));
                    }
                }


                if (!element.getElementsByTagName("UOMID").equals(null)) {

                    NodeList OutAddIDNode = element.getElementsByTagName("UOMID");
                    Element line = (Element) OutAddIDNode.item(0);

                    if (OutAddIDNode.getLength() > 0) {

                        UOMID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                int AutoIdStore = 0;
                AutoIdStore = i + 1;

                dbengine.insertMinDelQty(prdId, StoreID, QPBT, QPTaxAmt, MinDlvryQty, UOMID, QPAT);
            }
		           /* NodeList tblNewStoreIDInfoNode = doc.getElementsByTagName("NewStoreIDInfo");
		            for (int i = 0; i < tblNewStoreIDInfoNode.getLength(); i++)
		            {

		            	//<RetailerID>26542</RetailerID> <PDAStoreId>abc</PDAStoreId> </NewStoreIDInfo>

		            	String RetailerID="NA";
		            	String PDAStoreId="NA";


		                Element element = (Element) tblNewStoreIDInfoNode.item(i);

		                NodeList RetailerIDNode = element.getElementsByTagName("RetailerID");
		  	            Element line = (Element) RetailerIDNode.item(0);
		  	            RetailerID=xmlParser.getCharacterDataFromElement(line);


		                NodeList PDAStoreIdNode = element.getElementsByTagName("PDAStoreId");
		                line = (Element) PDAStoreIdNode.item(0);
		                PDAStoreId=xmlParser.getCharacterDataFromElement(line);


		               dbengine.savetblStoreIDCustomInfo(RetailerID,PDAStoreId);

		             }*/


            setmovie.director = "1";
            dbengine.close();
            return setmovie;

        } catch (Exception e) {

            // System.out.println("Getting Response by Shivam Exception occur in getNewStoreInfo :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }
    }

    public ServiceWorker getNewStoreInfo(Context ctx, String dateVAL, String uuid, String rID, String StoreIDNew, String retailer_name,
                                         String owner_name, String phoneSendValue, String address, String City, String Pincode,
                                         String Lat, String Long, String Accuracy, String locProvider, String StoreIDPDA,
                                         String emailID, String TinNo, String TradeChannelID, String storeTypeID,
                                         String SelectedStoreProductClassificationValue, String KeyAccount, String VisitStartTS,
                                         String VisitEndTS, String BatteryStatus, String CustomStringForServiceWorker) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);

        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnAddNewStore";
        final String METHOD_NAME = "fnAddNewStore";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        //Create request
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();


            client.addProperty("RouteID", rID.trim());
            client.addProperty("StoreID", StoreIDPDA.trim());
            client.addProperty("StoreName", retailer_name.trim());
            client.addProperty("RetailerName", owner_name.trim());
            client.addProperty("EmailId", emailID);
            client.addProperty("TinNo", TinNo);
            client.addProperty("RetailerContactNo", phoneSendValue.trim());
            client.addProperty("StoreAddress", address.trim());
            client.addProperty("StorePincode", Pincode.trim());
            client.addProperty("City", City.trim());
            client.addProperty("KeyAccount", KeyAccount);
            client.addProperty("TradeChannelID", TradeChannelID);
            client.addProperty("StoreAttrHierID", storeTypeID.trim());
            client.addProperty("StoreProductClassificationID", SelectedStoreProductClassificationValue);
            client.addProperty("ActualLatitude", Lat);
            client.addProperty("ActualLongitude", Long);
            client.addProperty("LocProvider", locProvider);
            client.addProperty("Accuracy", (int) Double.parseDouble(Accuracy));
            client.addProperty("VisitStartTS", VisitStartTS);
            client.addProperty("VisitEndTS", VisitEndTS);
            client.addProperty("Imei", uuid.trim());
            client.addProperty("XMLFullDate", "NA");
            client.addProperty("XMLReceiveDate", "NA");
            client.addProperty("BatteryStatus", BatteryStatus);
            client.addProperty("OutStat", 0);
            client.addProperty("CityId", 1);
            client.addProperty("AppVersion", dbengine.DATABASE_VERSION);
            client.addProperty("tblOrderStoreCategoryProductDetails", CustomStringForServiceWorker);


				/*
				 // System.out.println("Getting Response by Shivam RouteID :"+rID.trim());
				 // System.out.println("Getting Response by Shivam StoreID :"+StoreIDPDA.trim());
				 // System.out.println("Getting Response by Shivam StoreName :"+retailer_name.trim());
				 // System.out.println("Getting Response by Shivam RetailerName :"+owner_name.trim());
				 // System.out.println("Getting Response by Shivam EmailId :"+emailID);
				 // System.out.println("Getting Response by Shivam  TinNo :"+TinNo);
				 // System.out.println("Getting Response by Shivam  phoneSendValue.trim() :"+phoneSendValue.trim());
				 // System.out.println("Getting Response by Shivam PhoneNo :"+phoneSendValue.trim());
				 // System.out.println("Getting Response by Shivam address.trim()); :"+address.trim());
				 // System.out.println("Getting Response by Shivam  Pincode.trim()); :"+Pincode.trim());
				 // System.out.println("Getting Response by Shivam  City.trim() :"+City.trim());
				 // System.out.println("Getting Response by Shivam StorePincode :"+Pincode.trim());
				 // System.out.println("Getting Response by Shivam City :"+City.trim());
				 // System.out.println("Getting Response by Shivam KeyAccount :"+KeyAccount);



				 // System.out.println("Getting Response by Shivam TradeChannelID :"+TradeChannelID);
				 // System.out.println("Getting Response by Shivam storeTypeID :"+storeTypeID);
				 // System.out.println("Getting Response by Shivam SelectedStoreProductClassificationValue :"+SelectedStoreProductClassificationValue);
				 // System.out.println("Getting Response by Shivam Lat :"+Lat);
				 // System.out.println("Getting Response by Shivam Long :"+Long);
				 // System.out.println("Getting Response by Shivam locProvider :"+locProvider);
				 // System.out.println("Getting Response by Shivam (int) Double.parseDouble(Accuracy) :"+(int) Double.parseDouble(Accuracy));
				 // System.out.println("Getting Response by Shivam VisitStartTS :"+VisitStartTS);
				 // System.out.println("Getting Response by Shivam VisitEndTS :"+VisitEndTS);
				 // System.out.println("Getting Response by Shivam uuid :"+uuid);
				 // System.out.println("Getting Response by Shivam BatteryStatus :"+BatteryStatus);
				 // System.out.println("Getting Response by Shivam dbengine.DATABASE_VERSION :"+dbengine.DATABASE_VERSION);
				 // System.out.println("Getting Response by Shivam CustomStringForServiceWorker :"+CustomStringForServiceWorker);
				 */

            dbengine.open();


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            String resultString = androidHttpTransport.responseDump;
            // remove information XML,only retrieved results that returned

            //	responseBody = (SoapObject) responseBody.getProperty(1);

            //int count123=responseBody.getPropertyCount();
            // get information XMl of tables that is returned
            String name = responseBody.getProperty(0).toString();

            // System.out.println("Getting Response by Shivam :"+name);


            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            NodeList tblNewStoreAllInfoNode = doc.getElementsByTagName("NewStoreAllInfo");
            for (int i = 0; i < tblNewStoreAllInfoNode.getLength(); i++) {


		            	/* <StoreID>26542</StoreID> <StoreName>sunil -delhi</StoreName>
		            	 <StoreLatitude>28.800000000000000000000000</StoreLatitude>
		            	 <StoreLongitude>77.900000000000000000000000</StoreLongitude>
		            	 <StoreType>1</StoreType> <LastTransactionDate>Dec 4 201</LastTransactionDate>
		            	 <LastVisitDate>Dec 4 201</LastVisitDate> <Sstat>0</Sstat> <IsClose>0</IsClose>
		            	 <IsNextDat>0</IsNextDat> <RouteID>1</RouteID>*/

                String StoreID = "0";
                String StoreName = "NA";
                Double StoreLatitude = 0.0;
                Double StoreLongitude = 0.0;
                String StoreType = "0";
                String LastTransactionDate = "NA";
                String LastVisitDate = "NA";
                int Sstat = 0;
                int IsClose = 0;
                int IsNextDat = 0;
                int RouteID = 0;

                Element element = (Element) tblNewStoreAllInfoNode.item(i);

                NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                Element line = (Element) StoreIDNode.item(0);
                StoreID = xmlParser.getCharacterDataFromElement(line);

                NodeList StoreNameNode = element.getElementsByTagName("StoreName");
                line = (Element) StoreNameNode.item(0);
                StoreName = xmlParser.getCharacterDataFromElement(line);

                NodeList StoreLatitudeNode = element.getElementsByTagName("StoreLatitude");
                line = (Element) StoreLatitudeNode.item(0);
                StoreLatitude = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));

                NodeList StoreLongitudeNode = element.getElementsByTagName("StoreLongitude");
                line = (Element) StoreLongitudeNode.item(0);
                StoreLongitude = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));

                NodeList StoreTypeNode = element.getElementsByTagName("StoreType");
                line = (Element) StoreTypeNode.item(0);
                StoreType = xmlParser.getCharacterDataFromElement(line);

                NodeList LastTransactionDateNode = element.getElementsByTagName("LastTransactionDate");
                line = (Element) LastTransactionDateNode.item(0);
                LastTransactionDate = xmlParser.getCharacterDataFromElement(line);

                NodeList LastVisitDateNode = element.getElementsByTagName("LastVisitDate");
                line = (Element) LastVisitDateNode.item(0);
                LastVisitDate = xmlParser.getCharacterDataFromElement(line);

                NodeList SstatNode = element.getElementsByTagName("Sstat");
                line = (Element) SstatNode.item(0);
                Sstat = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                NodeList IsCloseNode = element.getElementsByTagName("IsClose");
                line = (Element) IsCloseNode.item(0);
                IsClose = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                NodeList IsNextDatNode = element.getElementsByTagName("IsNextDat");
                line = (Element) IsNextDatNode.item(0);
                IsNextDat = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                NodeList RouteIDNode = element.getElementsByTagName("RouteID");
                line = (Element) RouteIDNode.item(0);
                RouteID = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));

                int AutoIdStore = 0;
                ;
                AutoIdStore = dbengine.checkStoreListTableCount();

                AutoIdStore = AutoIdStore + 1;


		              /*  dbengine.saveSOAPdataStoreList(StoreID,StoreName,StoreType,StoreLatitude,StoreLongitude,LastVisitDate,LastTransactionDate,
								dateVAL.toString().trim(), AutoIdStore, Sstat,IsClose,IsNextDat,RouteID); */


                // dbengine.fnUpdateFlgForNewSoreInMainStoreTable(StoreID);

		              /*  Add_New_Store_NewFormat.ServiceWorkerDataComingFlag=1;
		                Add_New_Store_NewFormat.ServiceWorkerStoreID=StoreID;
		                Add_New_Store_NewFormat.ServiceWorkerResultSet=StoreName+"^"+StoreType+"^"+StoreLatitude+"^"+StoreLongitude
		                		+"^"+LastVisitDate+"^"+LastTransactionDate+"^"+dateVAL.toString().trim()+"^"+AutoIdStore+"^"+Sstat+"^"+IsClose+"^"+IsNextDat+"^"+RouteID;

		                */
            }


            NodeList tblNewStoreIDInfoNode = doc.getElementsByTagName("NewStoreIDInfo");
            for (int i = 0; i < tblNewStoreIDInfoNode.getLength(); i++) {

                /*<RetailerID>26542</RetailerID> <PDAStoreId>abc</PDAStoreId> </NewStoreIDInfo>*/

                String RetailerID = "NA";
                String PDAStoreId = "NA";


                Element element = (Element) tblNewStoreIDInfoNode.item(i);

                NodeList RetailerIDNode = element.getElementsByTagName("RetailerID");
                Element line = (Element) RetailerIDNode.item(0);
                RetailerID = xmlParser.getCharacterDataFromElement(line);


                NodeList PDAStoreIdNode = element.getElementsByTagName("PDAStoreId");
                line = (Element) PDAStoreIdNode.item(0);
                PDAStoreId = xmlParser.getCharacterDataFromElement(line);


                // dbengine.savetblStoreIDCustomInfo(RetailerID,PDAStoreId);

            }


            setmovie.director = "1";
            dbengine.close();
            return setmovie;

        } catch (Exception e) {

            // System.out.println("Getting Response by Shivam Exception occur in getNewStoreInfo :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }
    }

    public ServiceWorker getServerDateFromServer(Context ctx) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();


        final String SOAP_ACTION = "http://tempuri.org/fnGetServerDate";
        final String METHOD_NAME = "fnGetServerDate";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        decimalFormat.applyPattern(pattern);
        SoapObject table = null; // Contains table of dataset that returned
        // throug SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);


        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);


            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }

            if (chkTblStoreListContainsRow == 1) {
                if (table.getPropertyCount() >= 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        tableRow = (SoapObject) table.getProperty(i);
                        String ServerDate = "0";


                        if (tableRow.hasProperty("ServerDate")) {
                            if (tableRow.getProperty("ServerDate").toString().isEmpty()) {
                                ServerDate = "0";
                            } else {
                                ServerDate = tableRow.getProperty("ServerDate").toString().trim();

                            }
                        }
                        // System.out.println("Testing Date all from Server :"+ServerDate);

                        dbengine.updatetblAvailableVersionMstr(ServerDate);


                    }
                }

            }


            dbengine.close();        // #4

            setmovie.director = "1";

            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetProductListMRNew :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();
            return setmovie;
        }

    }


    public ServiceWorker getCallspToCheckForVisit(Context ctx, String dateVAL, String imei) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        final String SOAP_ACTION = "http://tempuri.org/CallspToCheckForVisit";
        final String METHOD_NAME = "CallspToCheckForVisit";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            client.addProperty("IMEINo", imei.toString());
            client.addProperty("bydate", dateVAL.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            //// System.out.println("Aman a8 :"+responseBody);


            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }


            int chkProductClassificationContainsRow = 0;


            if (soDiffg.hasProperty("NewDataSet")) {

                chkProductClassificationContainsRow = 1;
                dbengine.deletetblNoVisitStoreDetails();
            }


            if (chkProductClassificationContainsRow == 1) {
                int AutoIdStore = 0;
                if (table.getPropertyCount() > 0) {
                    for (int i = 0; i < table.getPropertyCount(); i++) {

                        tableRow = (SoapObject) table.getProperty(i);

                        int flgHasVisit = 0;

                        if (tableRow.hasProperty("flgHasVisit")) {
                            if (tableRow.getProperty("flgHasVisit").toString().isEmpty()) {
                                flgHasVisit = 0;
                            } else {
                                String abc = tableRow.getProperty("flgHasVisit").toString().trim();
                                flgHasVisit = Integer.parseInt(abc);
                            }
                        }


                        AutoIdStore = i + 1;

                        String ReasonId = "0";
                        String ReasonDescr = "0";

                        //dbengine.savetblStoreProductClassificationTypeListMstr(AutoIdStore,CategoryNodeID,CategoryNodeType,Category,ProductTypeNodeID,ProductTypeNodeType,ProductType,0,0,"NA");

                        dbengine.savetblNoVisitStoreDetails(imei, "NA", ReasonId, ReasonDescr, flgHasVisit, 0);
							/*dbengine.close();
							String[] aa= dbengine.fnGetALLDataInfo();
							dbengine.open();*/

                    }
                }

            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 39;
            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Arjun getDistributorTypeMstr: 3 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getCallspToGetReasonMasterForNoVisit(Context ctx, String dateVAL, String uuid) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/CallspToGetReasonMasterForNoVisit";
        final String METHOD_NAME = "CallspToGetReasonMasterForNoVisit";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());

            //client.addProperty("CityId", 1);

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            //// System.out.println("TradeChannel  a8 :"+responseBody);
            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }


            int chkTblTradeChannelContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkTblTradeChannelContainsRow = 1;
                dbengine.deletetblNoVisitReasonMaster();
            }


            if (chkTblTradeChannelContainsRow == 1) {
                int AutoIdStore = 0;
                if (table.getPropertyCount() > 1) {
                    for (int i = 0; i <= table.getPropertyCount() - 1; i++) {

                        tableRow = (SoapObject) table.getProperty(i);

                        String ReasonId = "0";
                        String ReasonDescr = "NA";
                        int FlgToShowTextBox = 0;
                        int flgSOApplicable = 0;
                        int flgDSRApplicable = 0;
                        int flgNoVisitOption = 0;
                        int SeqNo = 0;
                        int flgDelayedReason = 0;


                        if (tableRow.hasProperty("ReasonId")) {
                            if (tableRow.getProperty("ReasonId").toString().isEmpty()) {
                                ReasonId = "0";
                            } else {
                                ReasonId = tableRow.getProperty("ReasonId").toString().trim();
                                //TradeChannelID=Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("ReasonDescr")) {
                            if (tableRow.getProperty("ReasonDescr").toString().isEmpty()) {
                                ReasonDescr = "NA";
                            } else {
                                ReasonDescr = tableRow.getProperty("ReasonDescr").toString().trim();
                            }
                        }
                        if (tableRow.hasProperty("FlgToShowTextBox")) {
                            if (tableRow.getProperty("FlgToShowTextBox").toString().isEmpty()) {
                                FlgToShowTextBox = 0;
                            } else {
                                String abc = tableRow.getProperty("FlgToShowTextBox").toString().trim();
                                FlgToShowTextBox = Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("flgSOApplicable")) {
                            if (tableRow.getProperty("flgSOApplicable").toString().isEmpty()) {
                                flgSOApplicable = 0;
                            } else {
                                flgSOApplicable = Integer.parseInt(tableRow.getProperty("flgSOApplicable").toString().trim());
                            }
                        }
                        if (tableRow.hasProperty("flgDSRApplicable")) {
                            if (tableRow.getProperty("flgDSRApplicable").toString().isEmpty()) {
                                flgDSRApplicable = 0;
                            } else {
                                String abc = tableRow.getProperty("flgDSRApplicable").toString().trim();
                                flgDSRApplicable = Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("flgNoVisitOption")) {
                            if (tableRow.getProperty("flgNoVisitOption").toString().isEmpty()) {
                                flgNoVisitOption = 0;
                            } else {
                                String abc = tableRow.getProperty("flgNoVisitOption").toString().trim();
                                flgNoVisitOption = Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("SeqNo")) {
                            if (tableRow.getProperty("SeqNo").toString().isEmpty()) {
                                SeqNo = 0;
                            } else {
                                String abc = tableRow.getProperty("SeqNo").toString().trim();
                                SeqNo = Integer.parseInt(abc);
                            }
                        }
                        if (tableRow.hasProperty("flgDelayedReason")) {
                            if (tableRow.getProperty("flgDelayedReason").toString().isEmpty()) {
                                flgDelayedReason = 0;
                            } else {
                                String abc = tableRow.getProperty("flgDelayedReason").toString().trim();
                                flgDelayedReason = Integer.parseInt(abc);
                            }
                        }


                        AutoIdStore = i + 1;


                        //dbengine.savetblNoVisitReasonMaster(AutoIdStore,ReasonId,ReasonDescr,FlgToShowTextBox);
                        dbengine.savetblNoVisitReasonMaster(AutoIdStore, ReasonId, ReasonDescr, FlgToShowTextBox, flgSOApplicable, flgDSRApplicable, flgNoVisitOption, SeqNo, flgDelayedReason);


                    }
                }

            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 38;
            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Arjun getStoreTypeMstr: 2 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }


    public ServiceWorker getCallspSaveReasonForNoVisit(Context ctx, String dateVAL, String uuid, String ReasonId, String ReasonText) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/CallspSaveReasonForNoVisit";
        final String METHOD_NAME = "CallspSaveReasonForNoVisit";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;


        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("ReasonId", ReasonId.toString());
            client.addProperty("ReasonText", ReasonText.toString());


            //client.addProperty("CityId", 1);

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            //// System.out.println("TradeChannel  a8 :"+responseBody);
            SoapObject soDiffg = (SoapObject) responseBody.getProperty("diffgram");

            if (soDiffg.hasProperty("NewDataSet")) {
                // remove information XML,only retrieved results that returned
                responseBody = (SoapObject) responseBody.getProperty(1);

                // get information XMl of tables that is returned
                table = (SoapObject) responseBody.getProperty(0);
            } else {

            }


            int chkTblTradeChannelContainsRow = 0;

            if (soDiffg.hasProperty("NewDataSet")) {
                chkTblTradeChannelContainsRow = 1;
                // dbengine.deletetblTradeChannelMstr();
            }


            if (chkTblTradeChannelContainsRow == 1) {
                int AutoIdStore = 0;
                if (table.getPropertyCount() > 0) {
                    for (int i = 0; i < table.getPropertyCount(); i++) {

                        tableRow = (SoapObject) table.getProperty(i);


                        int RowId = 0;

                        if (tableRow.hasProperty("RowId")) {
                            if (tableRow.getProperty("RowId").toString().isEmpty()) {
                                RowId = 0;
                            } else {
                                String abc = tableRow.getProperty("RowId").toString().trim();
                                RowId = Integer.parseInt(abc);
                            }
                        }


                        LauncherActivity.RowId = RowId;

                    }
                }

            }


            dbengine.close();        // #4

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 38;
            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Arjun getStoreTypeMstr: 2 "+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker callGetLastVisitPOSDetails(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnCallLastVisitPOSMaterial";
        final String METHOD_NAME = "fnCallLastVisitPOSMaterial";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        //Create request
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //uuid="353552060530972";
            //dateVAL="21-05-2016";


            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("uuid", uuid.toString());
            client.addProperty("rID", rID.toString());
            //client.addProperty("ApplicationID", CommonInfo.Application_TypeID);


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            String resultString = androidHttpTransport.responseDump;
            // remove information XML,only retrieved results that returned

            //	responseBody = (SoapObject) responseBody.getProperty(1);

            //int count123=responseBody.getPropertyCount();
            // get information XMl of tables that is returned
            String name = responseBody.getProperty(0).toString();
            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            dbengine.Delete_tblLastVisitDetailsForPOS();


            NodeList tblSchemeStoreMappingNode = doc.getElementsByTagName("tblLastVisitPOSDate");
            for (int i = 0; i < tblSchemeStoreMappingNode.getLength(); i++) {
                String StoreID = "NA";
                String LastVisitDate = "NA";

                Element element = (Element) tblSchemeStoreMappingNode.item(i);

                NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                Element line = (Element) StoreIDNode.item(0);
                StoreID = xmlParser.getCharacterDataFromElement(line);

		               /* if(!element.getElementsByTagName("CurrentStockQty").equals(null))
		                {
			  	        	   NodeList CurrentStockQtyNode = element.getElementsByTagName("CurrentStockQty");
				               line = (Element) CurrentStockQtyNode.item(0);
				           	    if(CurrentStockQtyNode.getLength()>0)
					                {
				           	    	CurrentStockQty=xmlParser.getCharacterDataFromElement(line);
					                }
		           	     }*/

                NodeList LastVisitDateNode = element.getElementsByTagName("LastVisitDate");
                line = (Element) LastVisitDateNode.item(0);
                LastVisitDate = xmlParser.getCharacterDataFromElement(line);

                dbengine.savetblStorePOSLastVisitDateDetail(StoreID, LastVisitDate);

            }

            //  StoreID,POSMaterialID,POSMaterialDescr,CurrentStockQty,NewOrderQty
            //  ReturnQty,DamageQty
		           /* string xmlns="http://tempuri.org/"><NewDataSet>
		            <tblLastVisitPOSDate> <LastVisitDate>19 May 2016</LastVisitDate> </tblLastVisitPOSDate>
		            <tblLastVisitPOSDetails>
		            <StoreID>205</StoreID>
		            <POSMaterialID>1</POSMaterialID>
		            <POSMaterialDescr>HP Metal standee � 30 pc</POSMaterialDescr>
		            <CurrentStockQty>2</CurrentStockQty>
		            <NewOrderQty>900</NewOrderQty>
		            <ReturnQty>1</ReturnQty>
		            <DamageQty>1</DamageQty> */

            NodeList tblLastVisitPOSDetailsNode = doc.getElementsByTagName("tblLastVisitPOSDetails");
            for (int i = 0; i < tblLastVisitPOSDetailsNode.getLength(); i++) {
                String StoreID = "NA";
                String POSMaterialID = "NA";
                String POSMaterialDescr = "NA";
                String CurrentStockQty = "NA";
                String NewOrderQty = "NA";
                String ReturnQty = "NA";
                String DamageQty = "NA";

                Element element = (Element) tblLastVisitPOSDetailsNode.item(i);

                NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                Element line = (Element) StoreIDNode.item(0);
                StoreID = xmlParser.getCharacterDataFromElement(line);

                NodeList POSMaterialIDNode = element.getElementsByTagName("POSMaterialID");
                line = (Element) POSMaterialIDNode.item(0);
                POSMaterialID = xmlParser.getCharacterDataFromElement(line);

                NodeList POSMaterialDescrNode = element.getElementsByTagName("POSMaterialDescr");
                line = (Element) POSMaterialDescrNode.item(0);
                POSMaterialDescr = xmlParser.getCharacterDataFromElement(line);

                NodeList CurrentStockQtyNode = element.getElementsByTagName("CurrentStockQty");
                line = (Element) CurrentStockQtyNode.item(0);
                CurrentStockQty = xmlParser.getCharacterDataFromElement(line);

                NodeList NewOrderQtyNode = element.getElementsByTagName("NewOrderQty");
                line = (Element) NewOrderQtyNode.item(0);
                NewOrderQty = xmlParser.getCharacterDataFromElement(line);

                NodeList ReturnQtyNode = element.getElementsByTagName("ReturnQty");
                line = (Element) ReturnQtyNode.item(0);
                ReturnQty = xmlParser.getCharacterDataFromElement(line);

                NodeList DamageQtyNode = element.getElementsByTagName("DamageQty");
                line = (Element) DamageQtyNode.item(0);
                DamageQty = xmlParser.getCharacterDataFromElement(line);


                dbengine.savetblStorePOSLastVisitALLMaterialDetails(StoreID, POSMaterialID, POSMaterialDescr
                        , CurrentStockQty, NewOrderQty, ReturnQty, DamageQty);


            }


            setmovie.director = "1";
            dbengine.close();
            // System.out.println("ServiceWorkerNitish getAllNewSchemeStructure Inside");
            flagExecutedServiceSuccesfully = 4;
            return setmovie;

        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetIMEIVersionDetailStatus :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();

            return setmovie;
        }
    }


    public ServiceWorker getCallfnGetActualVsTargetReport(Context ctx, String uuid, String dateVAL) //(Context ctx,int ApplicationID,String uuid)
    {
        this.context = ctx;


        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnGetActualVsTargetReport";
        final String METHOD_NAME = "fnGetActualVsTargetReport";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        //Create request
        SoapObject table = null; //Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; //Its the client petition to the web service
        SoapObject tableRow = null; //Contains row of table
        SoapObject responseBody = null; //Contains XML content of dataset

        //DBHelper dbengine=new DBHelper(ctx);
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;
        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 0);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);
            //client.addProperty("ApplicationID", ApplicationID);
            //client.addProperty("uuid", uuid.toString());

            String rID = dbengine.GetActiveRouteIDSunilAgain();


            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("uuid", uuid.toString());
            client.addProperty("rID", Integer.parseInt(rID));


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            //  <tblSPGetDistributorDetails> <NodeID>1</NodeID> <Descr>SUDARSAN TRADERS</Descr> <Code>101338</Code> <PNodeID>8</PNodeID> </tblSPGetDistributorDetails>
            //  dbengine.deleteAllSingleCallWebServiceTable();


            int AutoId = 0;

            NodeList tblGetPDAQuestionDependentMstrNode = doc.getElementsByTagName("tblActualVsTargetReport");
            for (int i = 0; i < tblGetPDAQuestionDependentMstrNode.getLength(); i++) {


			                         /*String QuestionID="0";
			                         String OptionID="0";
			                         String DependentQuestionID="0";*/

                String Descr = "0";
                String TodayTarget = "0";
                String TodayAchieved = "0";
                String TodayBal = "0";
                String Todayflg = "0";
                String MonthTarget = "0";
                String MonthAchieved = "0";
                String MonthBal = "0";
                String Monthflg = "0";

                int ValTgtOrPrdctFlg = 1;


                Element element = (Element) tblGetPDAQuestionDependentMstrNode.item(i);


                if (!element.getElementsByTagName("Descr").equals(null)) {
                    NodeList QuestionIDNode = element.getElementsByTagName("Descr");
                    Element line = (Element) QuestionIDNode.item(0);
                    if (QuestionIDNode.getLength() > 0) {
                        Descr = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("TodayTarget").equals(null)) {
                    NodeList OptionIDNode = element.getElementsByTagName("TodayTarget");
                    Element line = (Element) OptionIDNode.item(0);
                    if (OptionIDNode.getLength() > 0) {
                        TodayTarget = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("TodayAchieved").equals(null)) {
                    NodeList DependentQuestionIDNode = element.getElementsByTagName("TodayAchieved");
                    Element line = (Element) DependentQuestionIDNode.item(0);
                    if (DependentQuestionIDNode.getLength() > 0) {
                        TodayAchieved = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("TodayBalance").equals(null)) {
                    NodeList TodayBalNode = element.getElementsByTagName("TodayBalance");
                    Element line = (Element) TodayBalNode.item(0);
                    if (TodayBalNode.getLength() > 0) {
                        TodayBal = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Todayflg").equals(null)) {
                    NodeList QuestionIDNode = element.getElementsByTagName("Todayflg");
                    Element line = (Element) QuestionIDNode.item(0);
                    if (QuestionIDNode.getLength() > 0) {
                        Todayflg = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("MonthTarget").equals(null)) {
                    NodeList OptionIDNode = element.getElementsByTagName("MonthTarget");
                    Element line = (Element) OptionIDNode.item(0);
                    if (OptionIDNode.getLength() > 0) {
                        MonthTarget = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("MonthAchieved").equals(null)) {
                    NodeList DependentQuestionIDNode = element.getElementsByTagName("MonthAchieved");
                    Element line = (Element) DependentQuestionIDNode.item(0);
                    if (DependentQuestionIDNode.getLength() > 0) {
                        MonthAchieved = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("MonthBalance").equals(null)) {
                    NodeList MonthBalNode = element.getElementsByTagName("MonthBalance");
                    Element line = (Element) MonthBalNode.item(0);
                    if (MonthBalNode.getLength() > 0) {
                        MonthBal = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Monthflg").equals(null)) {
                    NodeList DependentQuestionIDNode = element.getElementsByTagName("Monthflg");
                    Element line = (Element) DependentQuestionIDNode.item(0);
                    if (DependentQuestionIDNode.getLength() > 0) {
                        Monthflg = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                // System.out.println("QuestionID:"+QuestionID+"OptionID:"+OptionID+"DependentQuestionID:"+DependentQuestionID);
                AutoId = i + 1;
                //1 = ProductFlag for =ValTgtOrPrdctFlg
                dbengine.savetblTargetVsAchievedSummary(AutoId, Descr, TodayTarget, TodayAchieved, TodayBal,
                        Todayflg, MonthTarget, MonthAchieved, MonthBal, Monthflg, ValTgtOrPrdctFlg);

            }


//tblValueVolumeTarget
            NodeList tblValueVolumeTarget = doc.getElementsByTagName("tblValueVolumeTarget");
            for (int i = 0; i < tblValueVolumeTarget.getLength(); i++) {


						                         /*String QuestionID="0";
						                         String OptionID="0";
						                         String DependentQuestionID="0";*/

                String Descr = "0";
                String TodayTarget = "0";
                String TodayAchieved = "0";
                String TodayBal = "0";
                String Todayflg = "0";
                String MonthTarget = "0";
                String MonthAchieved = "0";
                String MonthBal = "0";
                String Monthflg = "0";

                int ValTgtOrPrdctFlg = 0;


                Element element = (Element) tblValueVolumeTarget.item(i);


                if (!element.getElementsByTagName("Descr").equals(null)) {
                    NodeList QuestionIDNode = element.getElementsByTagName("Descr");
                    Element line = (Element) QuestionIDNode.item(0);
                    if (QuestionIDNode.getLength() > 0) {
                        Descr = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("TodayTarget").equals(null)) {
                    NodeList OptionIDNode = element.getElementsByTagName("TodayTarget");
                    Element line = (Element) OptionIDNode.item(0);
                    if (OptionIDNode.getLength() > 0) {
                        TodayTarget = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("TodayAchieved").equals(null)) {
                    NodeList DependentQuestionIDNode = element.getElementsByTagName("TodayAchieved");
                    Element line = (Element) DependentQuestionIDNode.item(0);
                    if (DependentQuestionIDNode.getLength() > 0) {
                        TodayAchieved = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("TodayBalance").equals(null)) {
                    NodeList TodayBalNode = element.getElementsByTagName("TodayBalance");
                    Element line = (Element) TodayBalNode.item(0);
                    if (TodayBalNode.getLength() > 0) {
                        TodayBal = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Todayflg").equals(null)) {
                    NodeList QuestionIDNode = element.getElementsByTagName("Todayflg");
                    Element line = (Element) QuestionIDNode.item(0);
                    if (QuestionIDNode.getLength() > 0) {
                        Todayflg = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("MonthTarget").equals(null)) {
                    NodeList OptionIDNode = element.getElementsByTagName("MonthTarget");
                    Element line = (Element) OptionIDNode.item(0);
                    if (OptionIDNode.getLength() > 0) {
                        MonthTarget = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("MonthAchieved").equals(null)) {
                    NodeList DependentQuestionIDNode = element.getElementsByTagName("MonthAchieved");
                    Element line = (Element) DependentQuestionIDNode.item(0);
                    if (DependentQuestionIDNode.getLength() > 0) {
                        MonthAchieved = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("MonthBalance").equals(null)) {
                    NodeList MonthBalNode = element.getElementsByTagName("MonthBalance");
                    Element line = (Element) MonthBalNode.item(0);
                    if (MonthBalNode.getLength() > 0) {
                        MonthBal = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Monthflg").equals(null)) {
                    NodeList DependentQuestionIDNode = element.getElementsByTagName("Monthflg");
                    Element line = (Element) DependentQuestionIDNode.item(0);
                    if (DependentQuestionIDNode.getLength() > 0) {
                        Monthflg = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                // System.out.println("QuestionID:"+QuestionID+"OptionID:"+OptionID+"DependentQuestionID:"+DependentQuestionID);
                AutoId = i + 1;
                //1 = ProductFlag for =ValTgtOrPrdctFlg
                dbengine.savetblTargetVsAchievedSummary(AutoId, Descr, TodayTarget, TodayAchieved, TodayBal
                        , Todayflg, MonthTarget, MonthAchieved, MonthBal, Monthflg, ValTgtOrPrdctFlg);

            }


            NodeList tblActualVsTargetNote = doc.getElementsByTagName("tblActualVsTargetNote");
            for (int i = 0; i < tblActualVsTargetNote.getLength(); i++) {


						                         /*String QuestionID="0";
						                         String OptionID="0";
						                         String DependentQuestionID="0";*/

                String Descr = "0";


                int ValTgtOrPrdctFlg = 1;


                Element element = (Element) tblActualVsTargetNote.item(i);


                if (!element.getElementsByTagName("MsgToDisplay").equals(null)) {
                    NodeList QuestionIDNode = element.getElementsByTagName("MsgToDisplay");
                    Element line = (Element) QuestionIDNode.item(0);
                    if (QuestionIDNode.getLength() > 0) {
                        Descr = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblTargetVsAchievedNote(Descr);

            }

            dbengine.close();
            setmovie.director = "1";

            return setmovie;

        } catch (Exception e) {
            dbengine.close();
            System.out.println("Aman Exception occur in fnSingleCallAllWebService :" + e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();


            return setmovie;
        }
    }


    public ServiceWorker getfnGetStoreWiseTarget(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;


        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnGetStoreWiseTarget";
        final String METHOD_NAME = "fnGetStoreWiseTarget";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        //Create request
        SoapObject table = null; //Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; //Its the client petition to the web service
        SoapObject tableRow = null; //Contains row of table
        SoapObject responseBody = null; //Contains XML content of dataset

        //DBHelper dbengine=new DBHelper(ctx);
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;
        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 0);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);
            //client.addProperty("ApplicationID", ApplicationID);
            //client.addProperty("uuid", uuid.toString());

            //String rID=dbengine.GetActiveRouteIDSunilAgain();


            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();
            String RouteNodeType = "0";
            try {

                RouteNodeType = dbengine.FetchRouteType(rID);

                System.out.println("hi" + RouteNodeType);
            } catch (Exception e) {
                System.out.println("error" + e);
            }

            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("SysDate", SysDate.toString());
            client.addProperty("AppVersionID", dbengine.AppVersionID.toString());
            client.addProperty("RouteNodeType", RouteNodeType.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            //  <tblSPGetDistributorDetails> <NodeID>1</NodeID> <Descr>SUDARSAN TRADERS</Descr> <Code>101338</Code> <PNodeID>8</PNodeID> </tblSPGetDistributorDetails>
            //  dbengine.deleteAllSingleCallWebServiceTable();


            int AutoId = 0;

            NodeList tblGetPDAQuestionDependentMstrNode = doc.getElementsByTagName("tblStoreWiseTargetForDay");
            for (int i = 0; i < tblGetPDAQuestionDependentMstrNode.getLength(); i++) {


                String StoreID = "0";
                String TargetVal = "0";

                Element element = (Element) tblGetPDAQuestionDependentMstrNode.item(i);


                if (!element.getElementsByTagName("StoreID").equals(null)) {
                    NodeList QuestionIDNode = element.getElementsByTagName("StoreID");
                    Element line = (Element) QuestionIDNode.item(0);
                    if (QuestionIDNode.getLength() > 0) {
                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("TargetVal").equals(null)) {
                    NodeList OptionIDNode = element.getElementsByTagName("TargetVal");
                    Element line = (Element) OptionIDNode.item(0);
                    if (OptionIDNode.getLength() > 0) {
                        TargetVal = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                AutoId = i + 1;
                dbengine.savetblStoreWiseTarget(StoreID, TargetVal);


            }

            dbengine.close();
            setmovie.director = "1";

            return setmovie;

        } catch (Exception e) {
            dbengine.close();
            System.out.println("Aman Exception occur in fnSingleCallAllWebService :" + e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();


            return setmovie;
        }
    }

    public ServiceWorker fnGetIncentiveData(Context ctx, String bydate, String IMEINo) {
        this.context = ctx;
        String querryString = "";

        final String SOAP_ACTION = "http://tempuri.org/fnGetIncentiveData";
        final String METHOD_NAME = "fnGetIncentiveData";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        // through SoapObject
        SoapObject client = null; //Its the client petition to the web service
        SoapObject responseBody = null; //Contains XML content of dataset

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        //SoapObject param
        SoapSerializationEnvelope sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 0);

        ServiceWorker setmovie = new ServiceWorker();

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            client.addProperty("bydate", bydate.toString());
            client.addProperty("uuid", IMEINo.toString());

            sse.setOutputSoapObject(client);

            sse.bodyOut = client;

            androidHttpTransport.call(SOAP_ACTION, sse);

            responseBody = (SoapObject) sse.bodyIn;

            int totalCount = responseBody.getPropertyCount();

            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);

            dbengine.deleteIncentivesTbles();

            NodeList tblIncentiveMasterNode = doc.getElementsByTagName("tblIncentiveMainMaster");
            for (int i = 0; i < tblIncentiveMasterNode.getLength(); i++) {
                int IncId = 0;
                int OutputType = 0;
                String IncentiveName = "NA";
                String Earning = "0";
                String flgAcheived = "0"; // by default red color

                Element element = (Element) tblIncentiveMasterNode.item(i);

                if (!element.getElementsByTagName("IncId").equals(null)) {
                    NodeList IncIdNode = element.getElementsByTagName("IncId");
                    Element line = (Element) IncIdNode.item(0);
                    if (IncIdNode.getLength() > 0) {
                        IncId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("OutputType").equals(null)) {
                    NodeList OutputTypeNode = element.getElementsByTagName("OutputType");
                    Element line = (Element) OutputTypeNode.item(0);
                    if (OutputTypeNode.getLength() > 0) {
                        OutputType = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("IncentiveName").equals(null)) {
                    NodeList IncentiveNameNode = element.getElementsByTagName("IncentiveName");
                    Element line = (Element) IncentiveNameNode.item(0);
                    if (IncentiveNameNode.getLength() > 0) {
                        IncentiveName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("flgAcheived").equals(null)) {
                    NodeList flgAcheivedNode = element.getElementsByTagName("flgAcheived");
                    Element line = (Element) flgAcheivedNode.item(0);
                    if (flgAcheivedNode.getLength() > 0) {
                        flgAcheived = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Earning").equals(null)) {
                    NodeList EarningNode = element.getElementsByTagName("Earning");
                    Element line = (Element) EarningNode.item(0);
                    if (EarningNode.getLength() > 0) {
                        Earning = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblIncentiveMaster(IncId, OutputType, IncentiveName, flgAcheived, Earning);
                //System.out.println("MASTER TBL..."+IncId+"-"+OutputType+"-"+IncentiveName+"-"+flgAcheived+"-"+Earning);
            }


            NodeList tblIncentiveSecondaryMaster = doc.getElementsByTagName("tblIncentiveSecondaryMaster");
            for (int i = 0; i < tblIncentiveSecondaryMaster.getLength(); i++) {
                int IncSlabId = 0;
                int IncId = 0;
                int OutputType = 0;
                String IncSlabName = "NA";
                String Earning = "0";
                String flgAcheived = "0"; // by default red color

                Element element = (Element) tblIncentiveSecondaryMaster.item(i);


                if (!element.getElementsByTagName("IncSlabId").equals(null)) {
                    NodeList IncSlabIdNode = element.getElementsByTagName("IncSlabId");
                    Element line = (Element) IncSlabIdNode.item(0);
                    if (IncSlabIdNode.getLength() > 0) {
                        IncSlabId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("IncId").equals(null)) {
                    NodeList IncIdNode = element.getElementsByTagName("IncId");
                    Element line = (Element) IncIdNode.item(0);
                    if (IncIdNode.getLength() > 0) {
                        IncId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("OutputType").equals(null)) {
                    NodeList OutputTypeNode = element.getElementsByTagName("OutputType");
                    Element line = (Element) OutputTypeNode.item(0);
                    if (OutputTypeNode.getLength() > 0) {
                        OutputType = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("IncSlabName").equals(null)) {
                    NodeList IncSlabNameNode = element.getElementsByTagName("IncSlabName");
                    Element line = (Element) IncSlabNameNode.item(0);
                    if (IncSlabNameNode.getLength() > 0) {
                        IncSlabName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("flgAcheived").equals(null)) {
                    NodeList flgAcheivedNode = element.getElementsByTagName("flgAcheived");
                    Element line = (Element) flgAcheivedNode.item(0);
                    if (flgAcheivedNode.getLength() > 0) {
                        flgAcheived = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Earning").equals(null)) {
                    NodeList EarningNode = element.getElementsByTagName("Earning");
                    Element line = (Element) EarningNode.item(0);
                    if (EarningNode.getLength() > 0) {
                        Earning = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblIncentiveSeondaryMaster(IncSlabId, IncId, OutputType, IncSlabName, flgAcheived, Earning);
                //System.out.println("MASTER TBL..."+IncId+"-"+OutputType+"-"+IncentiveName+"-"+flgAcheived+"-"+Earning);
            }


            NodeList tblIncentiveDetailsDataNode = doc.getElementsByTagName("tblIncentiveDetailsData");
            String TableColumn[] = null;
            for (int i = 0; i < tblIncentiveDetailsDataNode.getLength(); i++) {
                Element element = (Element) tblIncentiveDetailsDataNode.item(i);

                if (i == 0) {
                    int tableColumn = element.getChildNodes().getLength() / 2;
                    TableColumn = new String[tableColumn];
                    int k = 0;

                    for (int j = 0; j < element.getChildNodes().getLength(); j++) {
                        if (element.getChildNodes().item(j).getNodeType() == 1) {
                            TableColumn[k] = element.getChildNodes().item(j).getNodeName().toString().trim();
                            k++;
                        }
                    }
                    if (TableColumn.length > 0) {
                        dbengine.CreateDynamicTables("tblIncentiveDetailsData", TableColumn);
                    }

                }

                int tableColumnValue = element.getChildNodes().getLength() / 2;
                String TableColumnValue[] = new String[tableColumnValue];
                int m = 0;

                for (int j = 0; j < element.getChildNodes().getLength(); j++) {

                    //System.out.println("H1 "+element.getChildNodes().item(j));
                    //System.out.println("H1 "+element.getChildNodes().item(j).getNodeType());
                    //System.out.println("H1 "+element.getChildNodes().item(j).getNodeName());
                    //System.out.println("H1 "+element.getChildNodes().item(j).getTextContent());//.item(j).getNodeValue());

                    if (element.getChildNodes().item(j).getNodeType() == 1) {
                        TableColumnValue[m] = element.getChildNodes().item(j).getTextContent().toString().trim();
                        m++;
                    }
                }
                if (TableColumnValue.length > 0) {
                    dbengine.insertDynamicTables("tblIncentiveDetailsData", TableColumn, TableColumnValue);
                }
            }

            NodeList tblIncentiveDetailsColumnsDescNode = doc.getElementsByTagName("tblIncentiveDetailsColumnsDesc");
            for (int i = 0; i < tblIncentiveDetailsColumnsDescNode.getLength(); i++) {
                int IncSlabId = 0;
                String ReportColumnName = "NA";
                String DisplayColumnName = "NA";

                Element element = (Element) tblIncentiveDetailsColumnsDescNode.item(i);

                if (!element.getElementsByTagName("IncSlabId").equals(null)) {
                    NodeList IncIdIncSlabIdNode = element.getElementsByTagName("IncSlabId");
                    Element line = (Element) IncIdIncSlabIdNode.item(0);
                    if (IncIdIncSlabIdNode.getLength() > 0) {
                        IncSlabId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("ReportColumnName").equals(null)) {
                    NodeList ReportColumnNameNode = element.getElementsByTagName("ReportColumnName");
                    Element line = (Element) ReportColumnNameNode.item(0);
                    if (ReportColumnNameNode.getLength() > 0) {
                        ReportColumnName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("DisplayColumnName").equals(null)) {
                    NodeList DisplayColumnNameNode = element.getElementsByTagName("DisplayColumnName");
                    Element line = (Element) DisplayColumnNameNode.item(0);
                    if (DisplayColumnNameNode.getLength() > 0) {
                        DisplayColumnName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblIncentiveDetailsColumnsDesc(IncSlabId, ReportColumnName, DisplayColumnName);
                //System.out.println("Column DESC TBL..."+IncId+"-"+ReportColumnName+"-"+DisplayColumnName);
            }

            NodeList tblTotalEarningNode = doc.getElementsByTagName("tblTotalEarning");
            for (int i = 0; i < tblTotalEarningNode.getLength(); i++) {
                String Total_Earning = "0";

                Element element = (Element) tblTotalEarningNode.item(i);

                if (!element.getElementsByTagName("Total_Earning").equals(null)) {
                    NodeList Total_EarningNode = element.getElementsByTagName("Total_Earning");
                    Element line = (Element) Total_EarningNode.item(0);
                    if (Total_EarningNode.getLength() > 0) {
                        Total_Earning = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblTotalEarning(Total_Earning);
            }


            NodeList tblIncentivePastDetailsDataNode = doc.getElementsByTagName("tblIncentivePastDetailsData");
            String TableColumnNew[] = null;
            for (int i = 0; i < tblIncentivePastDetailsDataNode.getLength(); i++) {
                Element element = (Element) tblIncentivePastDetailsDataNode.item(i);

                if (i == 0) {
                    int tableColumn = element.getChildNodes().getLength() / 2;
                    TableColumnNew = new String[tableColumn];
                    int k = 0;

                    for (int j = 0; j < element.getChildNodes().getLength(); j++) {
                        if (element.getChildNodes().item(j).getNodeType() == 1) {
                            TableColumnNew[k] = element.getChildNodes().item(j).getNodeName().toString().trim();
                            k++;
                        }
                    }
                    if (TableColumnNew.length > 0) {
                        dbengine.CreateDynamicTblIncPastDetails("tblIncentivePastDetailsData", TableColumnNew);
                    }
                }

                int tableColumnValue = element.getChildNodes().getLength() / 2;
                String TableColumnValue[] = new String[tableColumnValue];
                int m = 0;

                for (int j = 0; j < element.getChildNodes().getLength(); j++) {

                    System.out.println("H1 " + element.getChildNodes().item(j));
                    System.out.println("H1 " + element.getChildNodes().item(j).getNodeType());
                    System.out.println("H1 " + element.getChildNodes().item(j).getNodeName());
                    System.out.println("H1 " + element.getChildNodes().item(j).getTextContent());//.item(j).getNodeValue());

                    if (element.getChildNodes().item(j).getNodeType() == 1) {
                        TableColumnValue[m] = element.getChildNodes().item(j).getTextContent().toString().trim();
                        m++;
                    }
                }
                if (TableColumnValue.length > 0) {
                    dbengine.insertDynamicTables("tblIncentivePastDetailsData", TableColumnNew, TableColumnValue);
                }
            }


            NodeList tblIncentivePastDetailsColumnsDescNode = doc.getElementsByTagName("tblIncentivePastDetailsColumnsDesc");
            for (int i = 0; i < tblIncentivePastDetailsColumnsDescNode.getLength(); i++) {
                int IncSlabId = 0;
                String ReportColumnName = "0";
                String DisplayColumnName = "0";
                String Ordr = "0";

                Element element = (Element) tblIncentivePastDetailsColumnsDescNode.item(i);

                if (!element.getElementsByTagName("IncSlabId").equals(null)) {
                    NodeList IncSlabIdNode = element.getElementsByTagName("IncSlabId");
                    Element line = (Element) IncSlabIdNode.item(0);
                    if (IncSlabIdNode.getLength() > 0) {
                        IncSlabId = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                if (!element.getElementsByTagName("ReportColumnName").equals(null)) {
                    NodeList ReportColumnNameNode = element.getElementsByTagName("ReportColumnName");
                    Element line = (Element) ReportColumnNameNode.item(0);
                    if (ReportColumnNameNode.getLength() > 0) {
                        ReportColumnName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("DisplayColumnName").equals(null)) {
                    NodeList DisplayColumnNameNode = element.getElementsByTagName("DisplayColumnName");
                    Element line = (Element) DisplayColumnNameNode.item(0);
                    if (DisplayColumnNameNode.getLength() > 0) {
                        DisplayColumnName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Ordr").equals(null)) {
                    NodeList OrdrNode = element.getElementsByTagName("Ordr");
                    Element line = (Element) OrdrNode.item(0);
                    if (OrdrNode.getLength() > 0) {
                        Ordr = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblIncentivePastDetailsColumnsDesc(IncSlabId, ReportColumnName, DisplayColumnName, Ordr);
            }
            NodeList tblIncentiveMsgToDisplay = doc.getElementsByTagName("tblIncentiveMsgToDisplay");
            for (int i = 0; i < tblIncentiveMsgToDisplay.getLength(); i++) {

                String MsgToDisplay = "0";
                int flgBankDetailsToShow = 0;
                Element element = (Element) tblIncentiveMsgToDisplay.item(i);

                if (!element.getElementsByTagName("MsgToDisplay").equals(null)) {
                    NodeList MsgToDisplayNode = element.getElementsByTagName("MsgToDisplay");
                    Element line = (Element) MsgToDisplayNode.item(0);
                    if (MsgToDisplayNode.getLength() > 0) {
                        MsgToDisplay = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("flgBankDetailsToShow").equals(null)) {
                    NodeList flgBankDetailsToShowNode = element.getElementsByTagName("flgBankDetailsToShow");
                    Element line = (Element) flgBankDetailsToShowNode.item(0);
                    if (flgBankDetailsToShowNode.getLength() > 0) {
                        flgBankDetailsToShow = Integer.parseInt(xmlParser.getCharacterDataFromElement(line));
                    }
                }

                dbengine.savetblIncentiveMsgToDisplay(MsgToDisplay, flgBankDetailsToShow);
            }


            NodeList tblIncentiveBankDetails = doc.getElementsByTagName("tblIncentiveBankDetails");
            for (int i = 0; i < tblIncentiveBankDetails.getLength(); i++) {

                String LvlName = "0";
                String Value = "NA";

                Element element = (Element) tblIncentiveBankDetails.item(i);

                if (!element.getElementsByTagName("LvlName").equals(null)) {
                    NodeList LvlNameNode = element.getElementsByTagName("LvlName");
                    Element line = (Element) LvlNameNode.item(0);
                    if (LvlNameNode.getLength() > 0) {
                        LvlName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("Value").equals(null)) {
                    NodeList ValueNode = element.getElementsByTagName("Value");
                    Element line = (Element) ValueNode.item(0);
                    if (ValueNode.getLength() > 0) {
                        Value = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.savetblIncentiveBankDetails(LvlName, Value);
            }
            dbengine.close();
            setmovie.director = "1";

            return setmovie;

        } catch (Exception e) {
            dbengine.close();
            System.out.println("Aman Exception occur in fnIncentive :" + e.toString());

            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();

            return setmovie;
        }
    }
//nitika


    public ServiceWorker getfnCallspPDAGetDayAndMTDSummary(Context ctx, String dateVAL, String uuid) {
        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();
        //String Sstat = "0";

        final String SOAP_ACTION = "http://tempuri.org/fnCallspPDAGetDayAndMTDSummary";
        final String METHOD_NAME = "fnCallspPDAGetDayAndMTDSummary";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        // URL: L service
        decimalFormat.applyPattern(pattern);
        // NAMESPACE: must have in service page

        // METHOD_NAME: function in web service

        // SOAP_ACTION = NAMESPACE + METHOD_NAME

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";
            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            // System.out.println("Summary new SKUWise occur value bydate"+ dateVAL.toString());
            // System.out.println("Summary new SKUWise occur value IMEINo"+ uuid.toString());


            //client.addProperty("bydate", dateVAL.toString());

            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("bydate", dateVAL.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);

            // This step: get file XML
            responseBody = (SoapObject) sse.getResponse();
            // remove information XML,only retrieved results that returned
            responseBody = (SoapObject) responseBody.getProperty(1);

            // get information XMl of tables that is returned
            table = (SoapObject) responseBody.getProperty(0);

            // #1

            //////// System.out.println("Debug: " + "dbengine.open");

            //chkTblStoreListContainsRow
            if (table.getPropertyCount() >= 1) {
                chkTblStoreListContainsRow = 1;
            }
            //////// System.out.println("chkTblStoreListContainsRow: for StoreList "+ chkTblStoreListContainsRow);
            if (chkTblStoreListContainsRow == 1) {
                //////// System.out.println("table - prop count: "+ table.getPropertyCount());
                int AutoId = 0;
                for (int i = 0; i <= table.getPropertyCount() - 1; i++) {
                    tableRow = (SoapObject) table.getProperty(i);
                    //////// System.out.println("i value: "+ i);


                    String Measures = "0";
                    String TodaysSummary = "0";
                    String MTDSummary = "0";

                    String TableNo = "0";
                    String ColorCode = "0";


                    if (tableRow.hasProperty("Measures")) {
                        if (tableRow.getProperty("Measures").toString().isEmpty()) {
                            Measures = "0";
                        } else {
                            Measures = tableRow.getProperty("Measures").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("TodaysSummary")) {
                        if (tableRow.getProperty("TodaysSummary").toString().isEmpty()) {
                            TodaysSummary = "0";
                        } else {
                            TodaysSummary = tableRow.getProperty("TodaysSummary").toString().trim();
                        }
                    }
                    if (tableRow.hasProperty("MTDSummary")) {
                        if (tableRow.getProperty("MTDSummary").toString().isEmpty()) {
                            MTDSummary = "0";
                        } else {
                            MTDSummary = tableRow.getProperty("MTDSummary").toString().trim();
                        }
                    }


                    if (tableRow.hasProperty("TableNo")) {
                        if (tableRow.getProperty("TableNo").toString().isEmpty()) {
                            TableNo = "0";
                        } else {
                            TableNo = tableRow.getProperty("TableNo").toString().trim();
                        }
                    }


                    if (tableRow.hasProperty("ColorCode")) {
                        if (tableRow.getProperty("ColorCode").toString().isEmpty()) {
                            ColorCode = "0";
                        } else {
                            ColorCode = tableRow.getProperty("ColorCode").toString().trim();
                        }
                    }


                    AutoId = i + 1;


                    dbengine.savetblAllSummaryDayAndMTD(AutoId, Measures, TodaysSummary, MTDSummary, TableNo, ColorCode);


                }
            }

            dbengine.close();        // #4

				/*setmovie.director = tableRow.getProperty("Director").toString();
				setmovie.movie_name = tableRow.getProperty("Movie").toString();*/
            setmovie.director = "1";


            return setmovie;
            //return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetStoreListMR :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }


    }

    public ServiceWorker fnGetPDACollectionMaster(Context ctx, String uuid) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        String RouteType = "0";
        try {
			/*dbengine.open();
			String RouteID=dbengine.GetActiveRouteID();
			RouteType=dbengine.FetchRouteType(RouteID);
			dbengine.close();
			System.out.println("hi"+RouteType);*/
        } catch (Exception e) {
            System.out.println("error" + e);
        }

        dbengine.open();
        dbengine.deleteAllCollectionTables();

        final String SOAP_ACTION = "http://tempuri.org/fnGetPDACollectionMaster";
        final String METHOD_NAME = "fnGetPDACollectionMaster";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "ABC_CLASS_NAME" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            Date currDate = new Date();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);

            currSysDate = currDateFormat.format(currDate).toString();
            SysDate = currSysDate.trim().toString();

            client.addProperty("IMEINo", uuid.toString());


            client.addProperty("AppVersionID", dbengine.AppVersionID.toString());


            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            System.out.println("S1");

            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;


            int totalCount = responseBody.getPropertyCount();

            // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);
            System.out.println("shivam4");

            //   dbengine.open();

            NodeList tblBankMasterNode = doc.getElementsByTagName("tblBankMaster");
            for (int i = 0; i < tblBankMasterNode.getLength(); i++) {

                String BankId = "0";
                String BankName = "0";
                String LoginIdIns = "0";
                String TimeStampIns = "0";
                String LoginIdUpd = "0";
                String TimeStampUpd = "0";


                Element element = (Element) tblBankMasterNode.item(i);

                if (!element.getElementsByTagName("BankId").equals(null)) {

                    NodeList BankIdNode = element.getElementsByTagName("BankId");
                    Element line = (Element) BankIdNode.item(0);

                    if (BankIdNode.getLength() > 0) {

                        BankId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("BankName").equals(null)) {

                    NodeList BankNameNode = element.getElementsByTagName("BankName");
                    Element line = (Element) BankNameNode.item(0);

                    if (BankNameNode.getLength() > 0) {

                        BankName = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("LoginIdIns").equals(null)) {

                    NodeList LoginIdInsNode = element.getElementsByTagName("LoginIdIns");
                    Element line = (Element) LoginIdInsNode.item(0);

                    if (LoginIdInsNode.getLength() > 0) {

                        LoginIdIns = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("TimeStampIns").equals(null)) {

                    NodeList TimeStampInsNode = element.getElementsByTagName("TimeStampIns");
                    Element line = (Element) TimeStampInsNode.item(0);

                    if (TimeStampInsNode.getLength() > 0) {

                        TimeStampIns = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("LoginIdUpd").equals(null)) {

                    NodeList LoginIdUpdNode = element.getElementsByTagName("LoginIdUpd");
                    Element line = (Element) LoginIdUpdNode.item(0);

                    if (LoginIdUpdNode.getLength() > 0) {

                        LoginIdUpd = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("TimeStampUpd").equals(null)) {

                    NodeList TimeStampUpdNode = element.getElementsByTagName("TimeStampUpd");
                    Element line = (Element) TimeStampUpdNode.item(0);

                    if (TimeStampUpdNode.getLength() > 0) {

                        TimeStampUpd = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.savetblBankMaster(BankId, BankName, LoginIdIns, TimeStampIns, LoginIdUpd, TimeStampUpd);
            }

            NodeList tblInstrumentMasterNode = doc.getElementsByTagName("tblInstrumentMaster");
            for (int i = 0; i < tblInstrumentMasterNode.getLength(); i++) {

                String InstrumentModeId = "0";
                String InstrumentMode = "0";
                String InstrumentType = "0";


                Element element = (Element) tblInstrumentMasterNode.item(i);

                if (!element.getElementsByTagName("InstrumentModeId").equals(null)) {

                    NodeList InstrumentModeIdNode = element.getElementsByTagName("InstrumentModeId");
                    Element line = (Element) InstrumentModeIdNode.item(0);

                    if (InstrumentModeIdNode.getLength() > 0) {

                        InstrumentModeId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("InstrumentMode").equals(null)) {

                    NodeList InstrumentModeNode = element.getElementsByTagName("InstrumentMode");
                    Element line = (Element) InstrumentModeNode.item(0);

                    if (InstrumentModeNode.getLength() > 0) {

                        InstrumentMode = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("InstrumentType").equals(null)) {

                    NodeList InstrumentTypeNode = element.getElementsByTagName("InstrumentType");
                    Element line = (Element) InstrumentTypeNode.item(0);

                    if (InstrumentTypeNode.getLength() > 0) {

                        InstrumentType = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                dbengine.savetblInstrumentMaster(InstrumentModeId, InstrumentMode, InstrumentType);
            }

            setmovie.director = "1";
            // System.out.println("ServiceWorkerNitish getallStores Completed ");
            flagExecutedServiceSuccesfully = 40;
            return setmovie;


        } catch (Exception e) {

            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }
    }

    //outlet summary
    public ServiceWorker getPDAAddedOutletSummaryReport(Context ctx, String uuid, int flgDrillLevel) {

        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnGetPDAGetAddedOutletSummaryReport";
        final String METHOD_NAME = "fnGetPDAGetAddedOutletSummaryReport";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        //Create request
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();


        // // System.out.println("Kajol 100");

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            // // System.out.println("Kajol 101");
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("flgDrillLevel", flgDrillLevel);

            // // System.out.println("Kajol 102");
            sse.setOutputSoapObject(client);
            // // System.out.println("Kajol 103");
            sse.bodyOut = client;
            // // System.out.println("Kajol 104");

            androidHttpTransport.call(SOAP_ACTION, sse);

            // // System.out.println("Kajol 1");

            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            // // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);

            dbengine.droptblDAGetAddedOutletSummaryReport();
            dbengine.createtblDAGetAddedOutletSummaryReport();

            NodeList tblDAGetAddedOutletSummaryReport = doc.getElementsByTagName("tblDAGetAddedOutletSummaryReport");

            for (int i = 0; i < tblDAGetAddedOutletSummaryReport.getLength(); i++) {
                String Header = "0";
                String Child = "0";
                String TotalStores = "0";
                String Validated = "0";
                String Pending = "0";
                int FlgNormalOverall = 0;

                Element element = (Element) tblDAGetAddedOutletSummaryReport.item(i);

                NodeList HeaderNode = element.getElementsByTagName("Header");
                Element line = (Element) HeaderNode.item(0);
                if (HeaderNode.getLength() > 0) {
                    Header = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList ChildNode = element.getElementsByTagName("Child");
                line = (Element) ChildNode.item(0);
                if (ChildNode.getLength() > 0) {
                    Child = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList TotalStoresNode = element.getElementsByTagName("TotalStores");
                line = (Element) TotalStoresNode.item(0);
                if (TotalStoresNode.getLength() > 0) {
                    TotalStores = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList ValidatedNode = element.getElementsByTagName("Validated");
                line = (Element) ValidatedNode.item(0);
                if (ValidatedNode.getLength() > 0) {
                    Validated = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList PendingNode = element.getElementsByTagName("Pending");
                line = (Element) PendingNode.item(0);
                if (PendingNode.getLength() > 0) {
                    Pending = xmlParser.getCharacterDataFromElement(line);
                }

                dbengine.savetblDAGetAddedOutletSummaryReport(Header, Child, TotalStores, Validated, Pending, FlgNormalOverall);
                System.out.println("HEADER..." + Header);
            }

            NodeList tblDAGetAddedOutletSummaryOverallReport = doc.getElementsByTagName("tblDAGetAddedOutletSummaryOverallReport");

            for (int i = 0; i < tblDAGetAddedOutletSummaryOverallReport.getLength(); i++) {
                String Header = "0";
                String Child = "0";
                String TotalStores = "0";
                String Validated = "0";
                String Pending = "0";
                int FlgNormalOverall = 1;

                Element element = (Element) tblDAGetAddedOutletSummaryOverallReport.item(i);

                NodeList HeaderNode = element.getElementsByTagName("Header");
                Element line = (Element) HeaderNode.item(0);
                if (HeaderNode.getLength() > 0) {
                    Header = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList ChildNode = element.getElementsByTagName("Child");
                line = (Element) ChildNode.item(0);
                if (ChildNode.getLength() > 0) {
                    Child = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList TotalStoresNode = element.getElementsByTagName("TotalStores");
                line = (Element) TotalStoresNode.item(0);
                if (TotalStoresNode.getLength() > 0) {
                    TotalStores = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList ValidatedNode = element.getElementsByTagName("Validated");
                line = (Element) ValidatedNode.item(0);
                if (ValidatedNode.getLength() > 0) {
                    Validated = xmlParser.getCharacterDataFromElement(line);
                }

                NodeList PendingNode = element.getElementsByTagName("Pending");
                line = (Element) PendingNode.item(0);
                if (PendingNode.getLength() > 0) {
                    Pending = xmlParser.getCharacterDataFromElement(line);
                }

                dbengine.savetblDAGetAddedOutletSummaryReport(Header, Child, TotalStores, Validated, Pending, FlgNormalOverall);
                System.out.println("HEADER..." + Header);
            }

            setmovie.director = "1";
            dbengine.close();
            return setmovie;

        } catch (Exception e) {

            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }

    }

    public ServiceWorker fnGetStateCityListMstr(Context ctx, String uuid, String CurDate, int ApplicationID) {

        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);


        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnGetStateCityListMstr";
        final String METHOD_NAME = "fnGetStateCityListMstr";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        //Create request
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            client.addProperty("bydate", CurDate);
            client.addProperty("uuid", uuid.toString());
            //client.addProperty("DatabaseVersion","11");
            client.addProperty("ApplicationID", ApplicationID);


            // // System.out.println("Kajol 102");
            sse.setOutputSoapObject(client);
            // // System.out.println("Kajol 103");
            sse.bodyOut = client;
            // // System.out.println("Kajol 104");

            androidHttpTransport.call(SOAP_ACTION, sse);

            // // System.out.println("Kajol 1");

            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            // // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);
            dbengine.deletetblStateCityMaster();
            dbengine.open();


            NodeList tblStateCityMaster = doc.getElementsByTagName("tblStateCityMaster");
            for (int i = 0; i < tblStateCityMaster.getLength(); i++) {

                String StateID = "0";
                String State = "NA";
                String CityID = "0";
                String City = "NA";
                int CityDefault = 0;


                Element element = (Element) tblStateCityMaster.item(i);

                if (!element.getElementsByTagName("StateID").equals(null)) {
                    NodeList StateIDNodeID = element.getElementsByTagName("StateID");
                    Element line = (Element) StateIDNodeID.item(0);
                    if (StateIDNodeID.getLength() > 0) {
                        StateID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("State").equals(null)) {
                    NodeList StateNode = element.getElementsByTagName("State");
                    Element line = (Element) StateNode.item(0);
                    if (StateNode.getLength() > 0) {
                        State = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("CityID").equals(null)) {
                    NodeList CityIDNode = element.getElementsByTagName("CityID");
                    Element line = (Element) CityIDNode.item(0);
                    if (CityIDNode.getLength() > 0) {
                        CityID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("City").equals(null)) {
                    NodeList CityNode = element.getElementsByTagName("City");
                    Element line = (Element) CityNode.item(0);
                    if (CityNode.getLength() > 0) {
                        City = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("CityDefault").equals(null)) {
                    NodeList CityDefaultNode = element.getElementsByTagName("CityDefault");
                    Element line = (Element) CityDefaultNode.item(0);
                    if (CityDefaultNode.getLength() > 0) {
                        CityDefault = Integer.parseInt(xmlParser.getCharacterDataFromElement(line).trim());
                    }
                }


                dbengine.fnsavetblStateCityMaster(StateID, State, CityID, City, CityDefault);

            }


            setmovie.director = "1";
            dbengine.close();
            return setmovie;

        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetIMEIVersionDetailStatusNew :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }


    }

    public ServiceWorker getDsrRegistrationData(Context ctx, String IMEINo, String MobNo, String DOB) {

        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnPDAGetPersonDetailsForRegistration";
        final String METHOD_NAME = "fnPDAGetPersonDetailsForRegistration";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        //Create request
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();


        // // System.out.println("Kajol 100");

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            // // System.out.println("Kajol 101");
            client.addProperty("IMEINo", IMEINo.toString().trim());
            client.addProperty("MobNo", MobNo.toString().trim());
            client.addProperty("DOB", DOB.toString().trim());

            // // System.out.println("Kajol 102");
            sse.setOutputSoapObject(client);
            // // System.out.println("Kajol 103");
            sse.bodyOut = client;
            // // System.out.println("Kajol 104");

            androidHttpTransport.call(SOAP_ACTION, sse);

            // // System.out.println("Kajol 1");

            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            // // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);

            dbengine.Delete_tblUserRegistarationStatus();


            NodeList tblUserRegistarationStatusNode = doc.getElementsByTagName("tblUserRegistarationStatus");
            for (int i = 0; i < tblUserRegistarationStatusNode.getLength(); i++) {

                String Flag = "0";
                String MsgToDisplay = "0";

                Element element = (Element) tblUserRegistarationStatusNode.item(i);

                if (!element.getElementsByTagName("Flag").equals(null)) {
                    NodeList FlagNode = element.getElementsByTagName("Flag");
                    Element line = (Element) FlagNode.item(0);
                    if (FlagNode.getLength() > 0) {
                        Flag = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("MsgToDisplay").equals(null)) {
                    NodeList MsgToDisplayNode = element.getElementsByTagName("MsgToDisplay");
                    Element line = (Element) MsgToDisplayNode.item(0);
                    if (MsgToDisplayNode.getLength() > 0) {
                        MsgToDisplay = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                dbengine.Delete_tblDsrRegDetails();


                dbengine.savetblUserRegistarationStatus(Flag, MsgToDisplay);

            }

            NodeList tblUserRegisteredPersonalDetailsNode = doc.getElementsByTagName("tblUserRegisteredPersonalDetails");
            for (int i = 0; i < tblUserRegisteredPersonalDetailsNode.getLength(); i++) {
                String PersonNodeId = "0";
                String PersonNodeType = "0";
                String Name = "0";
                String ContactNo = "0";
                String DOB_string = "0";
                String SelfieName = "0";
                String SignImgName = "0";
                String BankAccountnumber = "0";
                String BankID = "0";
                String IFSCCode = "0";
                String flgUPIID = "0";
                String UPIID = "0";
                String SelfieNameURL = "0";
                String EmailId = "0";
                String AadhaarNumber = "0";

                Element element = (Element) tblUserRegisteredPersonalDetailsNode.item(i);

                if (!element.getElementsByTagName("Name").equals(null)) {
                    NodeList FirstNameNode = element.getElementsByTagName("Name");
                    Element line = (Element) FirstNameNode.item(0);
                    if (FirstNameNode.getLength() > 0) {
                        Name = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("ContactNo").equals(null)) {
                    NodeList ContactNoNode = element.getElementsByTagName("ContactNo");
                    Element line = (Element) ContactNoNode.item(0);
                    if (ContactNoNode.getLength() > 0) {
                        ContactNo = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("DOB").equals(null)) {
                    NodeList DOBNode = element.getElementsByTagName("DOB");
                    Element line = (Element) DOBNode.item(0);
                    if (DOBNode.getLength() > 0) {
                        DOB_string = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("SelfieName").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("SelfieName");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        SelfieName = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("SignImgName").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("SignImgName");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        SignImgName = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("BankAccountnumber").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("BankAccountnumber");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        BankAccountnumber = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("BankID").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("BankID");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        BankID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("IFSCCode").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("IFSCCode");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        IFSCCode = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("UPIID").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("UPIID");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        UPIID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("flgUPIID").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("flgUPIID");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        flgUPIID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("PersonNodeId").equals(null)) {
                    NodeList PersonNodeIdNode = element.getElementsByTagName("PersonNodeId");
                    Element line = (Element) PersonNodeIdNode.item(0);
                    if (PersonNodeIdNode.getLength() > 0) {
                        PersonNodeId = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("PersonNodeType").equals(null)) {
                    NodeList PersonNodeTypeNode = element.getElementsByTagName("PersonNodeType");
                    Element line = (Element) PersonNodeTypeNode.item(0);
                    if (PersonNodeTypeNode.getLength() > 0) {
                        PersonNodeType = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("SelfieNameURL").equals(null)) {
                    NodeList SelfieNameURLNode = element.getElementsByTagName("SelfieNameURL");
                    Element line = (Element) SelfieNameURLNode.item(0);
                    if (SelfieNameURLNode.getLength() > 0) {
                        SelfieNameURL = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("EmailId").equals(null)) {
                    NodeList EmailIdNode = element.getElementsByTagName("EmailId");
                    Element line = (Element) EmailIdNode.item(0);
                    if (EmailIdNode.getLength() > 0) {
                        EmailId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("AadhaarNumber").equals(null)) {
                    NodeList AadhaarNumberNode = element.getElementsByTagName("AadhaarNumber");
                    Element line = (Element) AadhaarNumberNode.item(0);
                    if (AadhaarNumberNode.getLength() > 0) {
                        AadhaarNumber = xmlParser.getCharacterDataFromElement(line);
                    }
                }
//AadhaarNumber
                if (SelfieNameURL != null && SelfieName != null) {
                    if ((!SelfieNameURL.equals("")) && (!SelfieName.equals("")) && (!SelfieNameURL.equals("0")) && (!SelfieName.equals("0"))) {
                        downLoadingSelfieImage(SelfieNameURL, SelfieName);
                    }
                }

                dbengine.savetblDsrRegDetails(PersonNodeId, PersonNodeType, Name, ContactNo, DOB_string, SelfieName, SignImgName, BankAccountnumber, BankID, IFSCCode, flgUPIID, UPIID, SelfieNameURL, EmailId,AadhaarNumber);

            }


            setmovie.director = "1";
            dbengine.close();
            return setmovie;

        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetIMEIVersionDetailStatusNew :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }


    }

    public ServiceWorker getDsrRegistrationDataWithIMEI(Context ctx, String IMEINo) {

        this.context = ctx;
        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/fnGetPersonDetailsForRegistrationAgainstIMEI";
        final String METHOD_NAME = "fnGetPersonDetailsForRegistrationAgainstIMEI";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;
        //Create request
        SoapObject table = null; // Contains table of dataset that returned
        // through SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();


        // // System.out.println("Kajol 100");

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);


            // // System.out.println("Kajol 101");
            client.addProperty("IMEINo", IMEINo.toString().trim());
			/*client.addProperty("MobNo", MobNo.toString().trim());
			client.addProperty("DOB", DOB.toString().trim());*/

            // // System.out.println("Kajol 102");
            sse.setOutputSoapObject(client);
            // // System.out.println("Kajol 103");
            sse.bodyOut = client;
            // // System.out.println("Kajol 104");

            androidHttpTransport.call(SOAP_ACTION, sse);

            // // System.out.println("Kajol 1");

            responseBody = (SoapObject) sse.bodyIn;
            // This step: get file XML
            //responseBody = (SoapObject) sse.getResponse();
            int totalCount = responseBody.getPropertyCount();

            // // System.out.println("Kajol 2 :"+totalCount);
            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            // // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);

            dbengine.Delete_tblUserRegistarationStatus();
            dbengine.Delete_tblDsrRegDetails();







			/*NodeList tblUserRegistarationStatusNode = doc.getElementsByTagName("tblUserRegistarationStatus");
			for (int i = 0; i < tblUserRegistarationStatusNode.getLength(); i++)
			{

				String Flag="0";
				String MsgToDisplay="0";

				Element element = (Element) tblUserRegistarationStatusNode.item(i);

				if(!element.getElementsByTagName("Flag").equals(null))
				{
					NodeList FlagNode = element.getElementsByTagName("Flag");
					Element line = (Element) FlagNode.item(0);
					if(FlagNode.getLength()>0)
					{
						Flag=xmlParser.getCharacterDataFromElement(line);
					}}
				if(!element.getElementsByTagName("MsgToDisplay").equals(null))
				{
					NodeList MsgToDisplayNode = element.getElementsByTagName("MsgToDisplay");
					Element	 line = (Element) MsgToDisplayNode.item(0);
					if(MsgToDisplayNode.getLength()>0)
					{
						MsgToDisplay=xmlParser.getCharacterDataFromElement(line);
					}}

				dbengine.savetblUserRegistarationStatus(Flag,MsgToDisplay);

			}*/

            NodeList tblUserRegisteredPersonalDetailsNode = doc.getElementsByTagName("tblUserRegisteredPersonalDetails");
            for (int i = 0; i < tblUserRegisteredPersonalDetailsNode.getLength(); i++) {
                String PersonNodeId = "0";
                String PersonNodeType = "0";
                String Name = "0";
                String ContactNo = "0";
                String DOB_string = "0";
                String SelfieName = "0";
                String SignImgName = "0";
                String BankAccountnumber = "0";
                String BankID = "0";
                String IFSCCode = "0";
                String flgUPIID = "0";
                String UPIID = "0";
                String SelfieNameURL = "0";
                String EmailId = "0";
                String AadhaarNumber = "0";

                Element element = (Element) tblUserRegisteredPersonalDetailsNode.item(i);

                if (!element.getElementsByTagName("Name").equals(null)) {
                    NodeList FirstNameNode = element.getElementsByTagName("Name");
                    Element line = (Element) FirstNameNode.item(0);
                    if (FirstNameNode.getLength() > 0) {
                        Name = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("ContactNo").equals(null)) {
                    NodeList ContactNoNode = element.getElementsByTagName("ContactNo");
                    Element line = (Element) ContactNoNode.item(0);
                    if (ContactNoNode.getLength() > 0) {
                        ContactNo = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("DOB").equals(null)) {
                    NodeList DOBNode = element.getElementsByTagName("DOB");
                    Element line = (Element) DOBNode.item(0);
                    if (DOBNode.getLength() > 0) {
                        DOB_string = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("SelfieName").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("SelfieName");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        SelfieName = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("SignImgName").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("SignImgName");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        SignImgName = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("BankAccountnumber").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("BankAccountnumber");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        BankAccountnumber = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("BankID").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("BankID");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        BankID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("IFSCCode").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("IFSCCode");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        IFSCCode = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("UPIID").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("UPIID");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        UPIID = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("flgUPIID").equals(null)) {
                    NodeList PhotoNameNode = element.getElementsByTagName("flgUPIID");
                    Element line = (Element) PhotoNameNode.item(0);
                    if (PhotoNameNode.getLength() > 0) {
                        flgUPIID = xmlParser.getCharacterDataFromElement(line);
                    }
                }


                if (!element.getElementsByTagName("AadhaarNumber").equals(null)) {
                    NodeList AadhaarNumberNode = element.getElementsByTagName("AadhaarNumber");
                    Element line = (Element) AadhaarNumberNode.item(0);
                    if (AadhaarNumberNode.getLength() > 0) {
                        AadhaarNumber = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("PersonNodeId").equals(null)) {
                    NodeList PersonNodeIdNode = element.getElementsByTagName("PersonNodeId");
                    Element line = (Element) PersonNodeIdNode.item(0);
                    if (PersonNodeIdNode.getLength() > 0) {
                        PersonNodeId = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("PersonNodeType").equals(null)) {
                    NodeList PersonNodeTypeNode = element.getElementsByTagName("PersonNodeType");
                    Element line = (Element) PersonNodeTypeNode.item(0);
                    if (PersonNodeTypeNode.getLength() > 0) {
                        PersonNodeType = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("SelfieNameURL").equals(null)) {
                    NodeList SelfieNameURLNode = element.getElementsByTagName("SelfieNameURL");
                    Element line = (Element) SelfieNameURLNode.item(0);
                    if (SelfieNameURLNode.getLength() > 0) {
                        SelfieNameURL = xmlParser.getCharacterDataFromElement(line);
                    }
                }
                if (!element.getElementsByTagName("EmailId").equals(null)) {
                    NodeList EmailIdNode = element.getElementsByTagName("EmailId");
                    Element line = (Element) EmailIdNode.item(0);
                    if (EmailIdNode.getLength() > 0) {
                        EmailId = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (SelfieNameURL != null && SelfieName != null) {
                    if ((!SelfieNameURL.equals("")) && (!SelfieName.equals("")) && (!SelfieNameURL.equals("0")) && (!SelfieName.equals("0"))) {
                        downLoadingSelfieImage(SelfieNameURL, SelfieName);
                    }
                }


                dbengine.savetblDsrRegDetails(PersonNodeId, PersonNodeType, Name, ContactNo, DOB_string, SelfieName, SignImgName, BankAccountnumber, BankID, IFSCCode, flgUPIID, UPIID, SelfieNameURL, EmailId, AadhaarNumber);

            }


            setmovie.director = "1";
            dbengine.close();
            return setmovie;

        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetIMEIVersionDetailStatusNew :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            dbengine.close();

            return setmovie;
        }


    }

    public ServiceWorker getStoreWiseOutStandings(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/fnGetStoreOutStandings";//GetProductListMRNewProductFilterTest";
        final String METHOD_NAME = "fnGetStoreOutStandings";//GetProductListMRNewProductFilterTest
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        decimalFormat.applyPattern(pattern);
        SoapObject table = null; // Contains table of dataset that returned
        // throug SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        //sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);


        String RouteNodeType = "0";
        try {
            RouteNodeType = dbengine.FetchRouteType(rID);
        } catch (Exception e) {
            System.out.println("error" + e);
        }


        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);
            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteType", RouteNodeType);
            //client.addProperty("SysDate", SysDate.toString());
            //client.addProperty("AppVersionID", dbengine.AppVersionID.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);
            client.addProperty("CoverageAreaNodeID", 0);
            client.addProperty("coverageAreaNodeType", 0);


			/*client.addProperty("bydate", dateVAL.toString());
			client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;
            String resultString = androidHttpTransport.responseDump;
            String name = responseBody.getProperty(0).toString();
            // This step: get file XML
			/*responseBody = (SoapObject) sse.getResponse();
			  String name=responseBody.getProperty(0).toString();*/

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);

            dbengine.Delete_tblLastOutstanding_for_refreshData();

            NodeList tblLastOutstanding = doc.getElementsByTagName("tblLastOutstanding");
            for (int i = 0; i < tblLastOutstanding.getLength(); i++) {


                String StoreID = "NA";
                Double Outstanding = 0.0;
                Double AmtOverdue = 0.0;


                Element element = (Element) tblLastOutstanding.item(i);

                if (!element.getElementsByTagName("Storeid").equals(null)) {

                    NodeList StoreIDNode = element.getElementsByTagName("Storeid");
                    Element line = (Element) StoreIDNode.item(0);

                    if (StoreIDNode.getLength() > 0) {

                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("OutStanding").equals(null)) {

                    NodeList OutstandingNode = element.getElementsByTagName("OutStanding");
                    Element line = (Element) OutstandingNode.item(0);

                    if (OutstandingNode.getLength() > 0) {

                        Outstanding = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));
                        Outstanding = Double.parseDouble(decimalFormat.format(Outstanding));
                    }
                }
                if (!element.getElementsByTagName("AmtOverdue").equals(null)) {

                    NodeList AmtOverdueNode = element.getElementsByTagName("AmtOverdue");
                    Element line = (Element) AmtOverdueNode.item(0);

                    if (AmtOverdueNode.getLength() > 0) {

                        AmtOverdue = Double.parseDouble(xmlParser.getCharacterDataFromElement(line));
                        AmtOverdue = Double.parseDouble(decimalFormat.format(AmtOverdue));
                    }
                }

                dbengine.savetblLastOutstanding(StoreID, Outstanding, AmtOverdue);
            }

            NodeList tblInvoiceLastVisitDetails = doc.getElementsByTagName("tblInvoiceLastVisitDetails");
            for (int i = 0; i < tblInvoiceLastVisitDetails.getLength(); i++) {


                String StoreID = "NA";
                String InvCode = "00";
                String InvDate = "NA";
                String OutstandingAmt = "0.0";
                String AmtOverdue = "0.0";


                Element element = (Element) tblInvoiceLastVisitDetails.item(i);

                if (!element.getElementsByTagName("Storeid").equals(null)) {

                    NodeList StoreIDNode = element.getElementsByTagName("Storeid");
                    Element line = (Element) StoreIDNode.item(0);

                    if (StoreIDNode.getLength() > 0) {

                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("InvCode").equals(null)) {

                    NodeList InvCodeNode = element.getElementsByTagName("InvCode");
                    Element line = (Element) InvCodeNode.item(0);

                    if (InvCodeNode.getLength() > 0) {

                        InvCode = xmlParser.getCharacterDataFromElement(line);

                    }
                }

                if (!element.getElementsByTagName("InvDate").equals(null)) {

                    NodeList InvDateNode = element.getElementsByTagName("InvDate");
                    Element line = (Element) InvDateNode.item(0);

                    if (InvDateNode.getLength() > 0) {

                        InvDate = xmlParser.getCharacterDataFromElement(line);

                    }
                }
                if (!element.getElementsByTagName("OutStandingAmt").equals(null)) {

                    NodeList OutstandingAmtNode = element.getElementsByTagName("OutStandingAmt");
                    Element line = (Element) OutstandingAmtNode.item(0);

                    if (OutstandingAmtNode.getLength() > 0) {

                        Double OutstandingAmtServer = Double.valueOf("" + Double.parseDouble(xmlParser.getCharacterDataFromElement(line)));
                        OutstandingAmt = "" + Double.parseDouble(decimalFormat.format(OutstandingAmtServer));
                    }
                }
                if (!element.getElementsByTagName("AmtOverdue").equals(null)) {

                    NodeList AmtOverdueNode = element.getElementsByTagName("AmtOverdue");
                    Element line = (Element) AmtOverdueNode.item(0);

                    if (AmtOverdueNode.getLength() > 0) {

                        Double AmtOverdueServer = Double.valueOf("" + Double.parseDouble(xmlParser.getCharacterDataFromElement(line)));
                        AmtOverdue = "" + Double.parseDouble(decimalFormat.format(AmtOverdueServer));
                    }
                }

                dbengine.savetblInvoiceLastVisitDetails(StoreID, InvCode, InvDate, OutstandingAmt, AmtOverdue);
            }
            dbengine.close();

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 1;
            // System.out.println("ServiceWorkerNitish getallProduct Inside");
            return setmovie;
//return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetProductListMRNew :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public ServiceWorker getProductListLastVisitStockOrOrderMstr(Context ctx, String dateVAL, String uuid, String rID) {
        this.context = ctx;

        DBAdapterKenya dbengine = new DBAdapterKenya(context);
        dbengine.open();

        final String SOAP_ACTION = "http://tempuri.org/fnGetProductListWithStockOrOrderLastVisit";//GetProductListMRNewProductFilterTest";
        final String METHOD_NAME = "fnGetProductListWithStockOrOrderLastVisit";//GetProductListMRNewProductFilterTest
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        decimalFormat.applyPattern(pattern);
        SoapObject table = null; // Contains table of dataset that returned
        // throug SoapObject
        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        //SoapObject param
        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;


        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        //sse.addMapping(NAMESPACE, "ServiceWorker", this.getClass());
        // Note if class name isn't "movie" ,you must change
        sse.dotNet = true; // if WebService written .Net is result=true
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);


        String RouteNodeType = "0";
        try {
            RouteNodeType = dbengine.FetchRouteType(rID);
        } catch (Exception e) {
            System.out.println("error" + e);
        }


        ServiceWorker setmovie = new ServiceWorker();
        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);

            //String dateVAL = "00.00.0000";

            //////// System.out.println("soap obj date: "+ dateVAL);
            client.addProperty("bydate", dateVAL.toString());
            client.addProperty("IMEINo", uuid.toString());
            client.addProperty("rID", rID.toString());
            client.addProperty("RouteType", RouteNodeType);
            //client.addProperty("SysDate", SysDate.toString());
            //client.addProperty("AppVersionID", dbengine.AppVersionID.toString());
            client.addProperty("flgAllRoutesData", CommonInfo.flgAllRoutesData);
            client.addProperty("CoverageAreaNodeID", 0);
            client.addProperty("coverageAreaNodeType", 0);


			/*client.addProperty("bydate", dateVAL.toString());
			client.addProperty("IMEINo", uuid.toString());*/

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;
            String resultString = androidHttpTransport.responseDump;
            String name = responseBody.getProperty(0).toString();
            // This step: get file XML
			/*responseBody = (SoapObject) sse.getResponse();
			  String name=responseBody.getProperty(0).toString();*/

            // System.out.println("Kajol 3 :"+name);

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);

            dbengine.deletetblProductListLastVisitStockOrOrderMstr();
            dbengine.deletetblFeedbackCompetitrMstrData();

            NodeList tblLastOutstanding = doc.getElementsByTagName("tblProductListLastVisitStockOrOrderMstr");
            for (int i = 0; i < tblLastOutstanding.getLength(); i++) {


                String StoreID = "NA";
                String PrdID = "0";


                Element element = (Element) tblLastOutstanding.item(i);

                if (!element.getElementsByTagName("StoreID").equals(null)) {

                    NodeList StoreIDNode = element.getElementsByTagName("StoreID");
                    Element line = (Element) StoreIDNode.item(0);

                    if (StoreIDNode.getLength() > 0) {

                        StoreID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("PrdID").equals(null)) {

                    NodeList PrdIDNode = element.getElementsByTagName("PrdID");
                    Element line = (Element) PrdIDNode.item(0);

                    if (PrdIDNode.getLength() > 0) {

                        PrdID = xmlParser.getCharacterDataFromElement(line);

                    }
                }

                dbengine.savetblProductListLastVisitStockOrOrderMstr(StoreID, PrdID);
            }

            NodeList tblCompetitorBrandCategory = doc.getElementsByTagName("tblCompetitorBrandCategory");
            for (int i = 0; i < tblCompetitorBrandCategory.getLength(); i++) {


                String CompetitorBrandID = "0";
                String CompetitorBrand = "0";
                String BusinessUnitID = "0";
                String BusinessUnit = "0";


                Element element = (Element) tblCompetitorBrandCategory.item(i);

                if (!element.getElementsByTagName("CompetitorBrandID").equals(null)) {

                    NodeList CompetitorBrandIDNode = element.getElementsByTagName("CompetitorBrandID");
                    Element line = (Element) CompetitorBrandIDNode.item(0);

                    if (CompetitorBrandIDNode.getLength() > 0) {

                        CompetitorBrandID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("CompetitorBrand").equals(null)) {

                    NodeList CompetitorBrandNode = element.getElementsByTagName("CompetitorBrand");
                    Element line = (Element) CompetitorBrandNode.item(0);

                    if (CompetitorBrandNode.getLength() > 0) {

                        CompetitorBrand = xmlParser.getCharacterDataFromElement(line);

                    }
                }
                if (!element.getElementsByTagName("BusinessUnitID").equals(null)) {

                    NodeList BusinessUnitIDNode = element.getElementsByTagName("BusinessUnitID");
                    Element line = (Element) BusinessUnitIDNode.item(0);

                    if (BusinessUnitIDNode.getLength() > 0) {

                        BusinessUnitID = xmlParser.getCharacterDataFromElement(line);
                    }
                }

                if (!element.getElementsByTagName("BusinessUnit").equals(null)) {

                    NodeList BusinessUnitNode = element.getElementsByTagName("BusinessUnit");
                    Element line = (Element) BusinessUnitNode.item(0);

                    if (BusinessUnitNode.getLength() > 0) {

                        BusinessUnit = xmlParser.getCharacterDataFromElement(line);

                    }
                }

                //dbengine.savetblProductListLastVisitStockOrOrderMstr(StoreID,PrdID);
                dbengine.savetblFeedbackCompetitrMstrData(CompetitorBrandID, CompetitorBrand, BusinessUnitID, BusinessUnit, 0, 0);
            }


            dbengine.close();

            setmovie.director = "1";
            flagExecutedServiceSuccesfully = 1;
            // System.out.println("ServiceWorkerNitish getallProduct Inside");
            return setmovie;
//return counts;
        } catch (Exception e) {

            // System.out.println("Aman Exception occur in GetProductListMRNew :"+e.toString());
            setmovie.director = e.toString();
            setmovie.movie_name = e.toString();
            flagExecutedServiceSuccesfully = 0;
            dbengine.close();
            return setmovie;
        }

    }

    public void downLoadingSelfieImage(String SelfieNameURL, String SelfieName) {
        String URL_String = SelfieNameURL;
        String Video_Name = SelfieName;

        try {

            URL url = new URL(URL_String);
            URLConnection connection = url.openConnection();
            HttpURLConnection urlConnection = (HttpURLConnection) connection;
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            String PATH = Environment.getExternalStorageDirectory() + "/" + CommonInfo.ImagesFolderServer + "/";

            File file2 = new File(PATH + Video_Name);
            if (file2.exists()) {
                file2.delete();
            }

            File file1 = new File(PATH);
            if (!file1.exists()) {
                file1.mkdirs();
            }


            File file = new File(file1, Video_Name);

            int size = connection.getContentLength();


            FileOutputStream fileOutput = new FileOutputStream(file);

            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[size];
            int bufferLength = 0;
            long total = 0;
            int current = 0;
            while ((bufferLength = inputStream.read(buffer)) != -1) {
                total += bufferLength;

                fileOutput.write(buffer, 0, bufferLength);
            }

            fileOutput.close();

        } catch (Exception e) {

        }

    }

    public String getCurrentDateTime(String uuid, int DatabaseVersion, int ApplicationID) {


        String serverTime = "";
        decimalFormat.applyPattern(pattern);

        int chkTblStoreListContainsRow = 1;
        StringReader read;
        InputSource inputstream;
        final String SOAP_ACTION = "http://tempuri.org/GetServerTime";
        final String METHOD_NAME = "GetServerTime";
        //final String METHOD_NAME = "GetIMEIVersionDetailStatusNewTest";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = UrlForWebService;

        SoapObject table = null; // Contains table of dataset that returned

        SoapObject client = null; // Its the client petition to the web service
        SoapObject tableRow = null; // Contains row of table
        SoapObject responseBody = null; // Contains XML content of dataset

        HttpTransportSE transport = null; // That call webservice
        SoapSerializationEnvelope sse = null;

        sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        sse.dotNet = true;
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);

        ServiceWorker setmovie = new ServiceWorker();

        try {
            client = new SoapObject(NAMESPACE, METHOD_NAME);
            client.addProperty("uuid", uuid.toString());
            client.addProperty("DatabaseVersion", DatabaseVersion);
            client.addProperty("ApplicationID", ApplicationID);

            sse.setOutputSoapObject(client);
            sse.bodyOut = client;
            androidHttpTransport.call(SOAP_ACTION, sse);
            responseBody = (SoapObject) sse.bodyIn;
            int totalCount = responseBody.getPropertyCount();

            String resultString = androidHttpTransport.responseDump;

            String name = responseBody.getProperty(0).toString();

            XMLParser xmlParser = new XMLParser();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(name));
            Document doc = db.parse(is);


            NodeList tblBloodGroupNode = doc.getElementsByTagName("tblServerTime");


            Element element = (Element) tblBloodGroupNode.item(0);

            NodeList BloddGroupsNode = element.getElementsByTagName("ServerTime");
            Element line = (Element) BloddGroupsNode.item(0);
            if (BloddGroupsNode.getLength() > 0) {
                serverTime = xmlParser.getCharacterDataFromElement(line);
            }


            return serverTime;
        } catch (Exception e) {


            return serverTime;
        }
    }
}
