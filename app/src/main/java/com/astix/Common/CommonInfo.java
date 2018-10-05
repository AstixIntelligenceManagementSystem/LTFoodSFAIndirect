package com.astix.Common;

import android.net.Uri;

import java.io.File;

public class CommonInfo
{
//flgFilterProductOnCategoryAndSearchBasis--1 = Both Category And Seacrh,2 = Category,3=Only Search

	// Its for Live Path on 194 Server




	  public static File imageF_savedInstance=null;
	    public static String imageName_savedInstance=null;
	    public static String clickedTagPhoto_savedInstance=null;
	    public static Uri uriSavedImage_savedInstance=null;

	    public static String imei="";
	    public static String SalesQuoteId="BLANK";
	    public static String quatationFlag="";
	    public static String fileContent="";
	    public static String prcID="NULL";
	    public static String newQuottionID="NULL";
	    public static String globalValueOfPaymentStage="0"+"_"+"0"+"_"+"0";



	    public static String WebServicePath="http://103.20.212.194/WebServiceAndroidLTFoodsLive/Service.asmx";
	    public static String VersionDownloadPath="http://103.20.212.194/downloads/";
        public static String VersionDownloadAPKName="GTFieldOperations.apk";
		//public static String VersionDownloadAPKName="LTACESFA.apk"; // name change according to varun sir mail on 17 jan 2018

		public static String DATABASE_NAME = "DbTJUKSFAApp";

		public static int AnyVisit = 0;

		public static int DATABASE_VERSIONID = 85;      // put this field value based on value in table on the server
		public static String AppVersionID = "1.21";   // put this field value based on value in table on the server
		public static int Application_TypeID = 2; //1=Parag Store Mapping,2=Parag SFA Indirect,3=Parag SFA Direct

		public static String OrderSyncPath="http://103.20.212.194/ReadXML_LTFoodsLive/DefaultSFA.aspx";
		public static String ImageSyncPath="http://103.20.212.194/ReadXML_LTFoodsImagesLive/Default.aspx";

		public static String OrderTextSyncPath="http://103.20.212.194/ReadTxtFileForLTFoodsLive/default.aspx";

		public static String InvoiceSyncPath="http://103.20.212.194/ReadXML_LTFoodInvoiceLive/Default.aspx";

	    public static String DistributorSyncPath="http://103.20.212.194/ReadXML_LTFoodsSFADistributionLive/Default.aspx";

		public static String OrderXMLFolder="LTACESFAXml";
		public static String ImagesFolder="LTACESFAImages";
		public static String VideoFolder="VideoLTFOODS";
	    public static String ImagesFolderServer="LTACESFAImagesServer";
	    public static String TextFileFolder="LTACETextFile";
	public static String TextFileName="LtFoodAllDetails";
	public static String TextFileArrayName="AllDetails";
	    public static String InvoiceXMLFolder="LTACEInvoiceXml";
		public static String FinalLatLngJsonFile="LTACESFAFinalLatLngJson";

		public static String AppLatLngJsonFile="LTACESFALatLngJson";

public static String CompetitorImagesFolder=".CompetitorSFAImages";
		public static int DistanceRange=3000;
	    public static String SalesPersonTodaysTargetMsg="";
	    public static final String Preference="LTFoodsPrefrence";
	    public static final String DistributorXMLFolder="LTFoodsDistributorXMLFolder";
		public static int flgAllRoutesData=1;

        public static String activityFrom="AllButtonActivity";
        public static String ActiveRouteSM="0";










	// Its for Stagging Path on 194 Server




/*

	    public static File imageF_savedInstance=null;
	    public static String imageName_savedInstance=null;
	    public static String clickedTagPhoto_savedInstance=null;
	    public static Uri uriSavedImage_savedInstance=null;

	    public static String imei="";
	    public static String SalesQuoteId="BLANK";
	    public static String quatationFlag="";
	    public static String fileContent="";
	    public static String prcID="NULL";
	    public static String newQuottionID="NULL";
	    public static String globalValueOfPaymentStage="0"+"_"+"0"+"_"+"0";



	    public static String WebServicePath="http://103.20.212.194/WebServiceAndroidLTFoodsStagging/Service.asmx";
	    public static String VersionDownloadPath="http://103.20.212.194/downloads/";
		  public static String VersionDownloadAPKName="GTFieldOperationsStagging.apk";
		//public static String VersionDownloadAPKName="LTACESFAStagging.apk"; // name change according to varun sir mail on 17 jan 2018


		public static String DATABASE_NAME = "DbTJUKSFAApp";

		public static int AnyVisit = 0;

		public static int DATABASE_VERSIONID = 45;      // put this field value based on value in table on the server
		public static String AppVersionID = "1.20";   // put this field value based on value in table on the server
		public static int Application_TypeID = 2; //1=Parag Store Mapping,2=Parag SFA Indirect,3=Parag SFA Direct

		public static String OrderSyncPath="http://103.20.212.194/ReadXML_LTFoodsStagging/DefaultSFA.aspx";
		public static String ImageSyncPath="http://103.20.212.194/ReadXML_LTFoodsImagesStagging/Default.aspx";

		public static String OrderTextSyncPath="http://103.20.212.194/ReadTxtFileForLTFoodsStagging/default.aspx";

		public static String InvoiceSyncPath="http://103.20.212.194/ReadXML_LTFoodInvoiceStagging/Default.aspx";


	    public static String DistributorSyncPath="http://103.20.212.194/ReadXML_LTFoodsSFADistributionStagging/Default.aspx";

		public static String OrderXMLFolder="LTACESFAXml";
		public static String ImagesFolder="LTACESFAImages";
		public static String VideoFolder="VideoLTFOODS";
	    public static String TextFileFolder="LTACETextFile";
	    public static String InvoiceXMLFolder="LTACEInvoiceXml";
		public static String FinalLatLngJsonFile="LTACESFAFinalLatLngJson";

		public static String AppLatLngJsonFile="LTACESFALatLngJson";
public static String CompetitorImagesFolder=".CompetitorSFAImages";
		public static int DistanceRange=3000;
	    public static String SalesPersonTodaysTargetMsg="";
	    public static final String Preference="LTFoodsPrefrence";
	    public static final String DistributorXMLFolder="LTFoodsDistributorXMLFolder";
		public static int flgAllRoutesData=1;

	    public static String activityFrom="AllButtonActivity";

	    public static String ActiveRouteSM="0";
*/





	// Its for Test  Path on 194 server for SFA


/*


	public static File imageF_savedInstance = null;
	public static String imageName_savedInstance = null;
	public static String clickedTagPhoto_savedInstance = null;
	public static Uri uriSavedImage_savedInstance = null;

	public static String imei = "";
	public static String SalesQuoteId = "BLANK";
	public static String quatationFlag = "";
	public static String fileContent = "";
	public static String prcID = "NULL";
	public static String newQuottionID = "NULL";
	public static String globalValueOfPaymentStage = "0" + "_" + "0" + "_" + "0";


	public static String WebServicePath = "http://103.20.212.194/WebServiceAndroidLTFoodsTest/Service.asmx";
	public static String VersionDownloadPath = "http://103.20.212.194/downloads/";
	public static String VersionDownloadAPKName = "GTFieldOperationsTest.apk";
	//public static String VersionDownloadAPKName="LTACESFATest.apk"; // name change according to varun sir mail on 17 jan 2018


	public static String DATABASE_NAME = "DbTJUKSFAApp";

	public static int AnyVisit = 0;

	public static int DATABASE_VERSIONID = 56;      // put this field value based on value in table on the server
	public static String AppVersionID = "1.13";   // put this field value based on value in table on the server

	public static int Application_TypeID = 2; //1=Parag Store Mapping,2=Parag SFA Indirect,3=Parag SFA Direct

	public static String OrderSyncPath = "http://103.20.212.194/ReadXML_LTFoodsTest/DefaultSFA.aspx";
	public static String ImageSyncPath = "http://103.20.212.194/ReadXML_LTFoodsImagesTest/Default.aspx";

	public static String OrderTextSyncPath = "http://103.20.212.194/ReadTxtFileForLTFoodsTest/default.aspx";

	public static String InvoiceSyncPath = "http://103.20.212.194/ReadXML_LTFoodInvoiceTest/Default.aspx";

	public static String DistributorSyncPath = "http://103.20.212.194/ReadXML_LTFoodsSFADistributionTest/Default.aspx";

	public static String CompetitorImagesFolder = ".CompetitorSFAImages";
	public static String OrderXMLFolder = "LTACESFAXml";
	public static String ImagesFolder = "LTACESFAImages";
	public static String ImagesFolderServer = "LTACESFAImagesServer";
	public static String VideoFolder = "VideoLTFOODS";
	public static String TextFileFolder = "LTACETextFile";
	public static String InvoiceXMLFolder = "LTACEInvoiceXml";
	public static String FinalLatLngJsonFile = "LTACESFAFinalLatLngJson";

	public static String AppLatLngJsonFile = "LTACESFALatLngJson";

	public static int DistanceRange = 3000;
	public static String SalesPersonTodaysTargetMsg = "";
	public static final String Preference = "LTFoodsPrefrence";
	public static final String DistributorXMLFolder = "LTFoodsDistributorXMLFolder";
	public static int flgAllRoutesData = 1;
	public static String activityFrom = "AllButtonActivity";

	public static String ActiveRouteSM = "0";

*/




	// Its for Development  Path on 194 server for SFA







/*




   	    public static File imageF_savedInstance=null;
	    public static String imageName_savedInstance=null;
	    public static String clickedTagPhoto_savedInstance=null;
	    public static Uri uriSavedImage_savedInstance=null;

	    public static String imei="";
	    public static String SalesQuoteId="BLANK";
	    public static String quatationFlag="";
	    public static String fileContent="";
	    public static String prcID="NULL";
	    public static String newQuottionID="NULL";
	    public static String globalValueOfPaymentStage="0"+"_"+"0"+"_"+"0";



	    public static String WebServicePath="http://103.20.212.194/WebServiceAndroidLTFoodsDevelopment/Service.asmx";
	    public static String VersionDownloadPath="http://103.20.212.194/downloads/";
		  public static String VersionDownloadAPKName="GTFieldOperationsDev.apk";
		//public static String VersionDownloadAPKName="LTACESFADev.apk"; // name change according to varun sir mail on 17 jan 2018


		public static String DATABASE_NAME = "DbApp";

		public static int AnyVisit = 0;

		public static int DATABASE_VERSIONID = 22;      // put this field value based on value in table on the server
		public static String AppVersionID = "1.5";   // put this field value based on value in table on the server
		public static int Application_TypeID = 2; //1=Parag Store Mapping,2=Parag SFA Indirect,3=Parag SFA Direct

		public static String OrderSyncPath="http://103.20.212.194/ReadXML_LTFoodsDevelopment/DefaultSFA.aspx";
		public static String ImageSyncPath="http://103.20.212.194/ReadXML_LTFoodsImagesDevelopment/Default.aspx";

		public static String OrderTextSyncPath="http://103.20.212.194/ReadTxtFileForLTFoodsDevelopment/default.aspx";

		public static String InvoiceSyncPath="http://103.20.212.194/ReadXML_LTFoodInvoiceDevelopment/Default.aspx";

	    public static String DistributorSyncPath="http://103.20.212.194/ReadXML_LTFoodsSFADistributionDevelopment/Default.aspx";

		public static String OrderXMLFolder="LTACESFAXml";
		public static String ImagesFolder="LTACESFAImages";
	public static String CompetitorImagesFolder=".CompetitorSFAImages";
	    public static String ImagesFolderServer="LTACESFAImagesServer";
	    public static String VideoFolder="VideoLTFOODS";
	    public static String TextFileFolder="LTACETextFile";
	    public static String InvoiceXMLFolder="LTACEInvoiceXml";
		public static String FinalLatLngJsonFile="LTACESFAFinalLatLngJson";

		public static String AppLatLngJsonFile="LTACESFALatLngJson";

		public static int DistanceRange=3000;
	    public static String SalesPersonTodaysTargetMsg="";
	    public static final String Preference="LTFoodsPrefrence";
	    public static final String DistributorXMLFolder="LTFoodsDistributorXMLFolder";
		public static int flgAllRoutesData=1;
        public static String activityFrom="AllButtonActivity";


	    public static String ActiveRouteSM="0";






*/












// Its for Test Release  Path on 194 server for SFA




/*
	public static File imageF_savedInstance=null;
	public static String imageName_savedInstance=null;
	public static String clickedTagPhoto_savedInstance=null;
	public static Uri uriSavedImage_savedInstance=null;

	public static String imei="";
	public static String SalesQuoteId="BLANK";
	public static String quatationFlag="";
	public static String fileContent="";
	public static String prcID="NULL";
	public static String newQuottionID="NULL";
	public static String globalValueOfPaymentStage="0"+"_"+"0"+"_"+"0";



	public static String WebServicePath="http://103.20.212.194/WebServiceAndroidLTFoodsTestRelease/Service.asmx";
	public static String VersionDownloadPath="http://103.20.212.194/downloads/";
	public static String VersionDownloadAPKName="GTFieldOperationsTestRelease.apk";
	//public static String VersionDownloadAPKName="LTACESFADev.apk"; // name change according to varun sir mail on 17 jan 2018


	public static String DATABASE_NAME = "DbTJUKSFAApp";

	public static int AnyVisit = 0;

	public static int DATABASE_VERSIONID = 134;      // put this field value based on value in table on the server
	public static String AppVersionID = "1.47";   // put this field value based on value in table on the server
	public static int Application_TypeID = 2; //1=Parag Store Mapping,2=Parag SFA Indirect,3=Parag SFA Direct

	public static String OrderSyncPath="http://103.20.212.194/ReadXML_LTFoodsTestRelease/DefaultSFA.aspx";
	public static String ImageSyncPath="http://103.20.212.194/ReadXML_LTFoodsImagesTestRelease/Default.aspx";

	public static String OrderTextSyncPath="http://103.20.212.194/ReadTxtFileForLTFoodsTestRelease/default.aspx";

	public static String InvoiceSyncPath="http://103.20.212.194/ReadXML_LTFoodInvoiceTestRelease/Default.aspx";

	public static String DistributorSyncPath="http://103.20.212.194/ReadXML_LTFoodsSFADistributionTestRelease/Default.aspx";

	public static String OrderXMLFolder="LTACESFAXml";
	public static String ImagesFolder="LTACESFAImages";
	public static String ImagesFolderServer="LTACESFAImagesServer";
	public static String VideoFolder="VideoLTFOODS";
	public static String TextFileFolder="LTACETextFile";
	public static String InvoiceXMLFolder="LTACEInvoiceXml";
	public static String FinalLatLngJsonFile="LTACESFAFinalLatLngJson";

	public static String AppLatLngJsonFile="LTACESFALatLngJson";
	public static String CompetitorImagesFolder=".CompetitorSFAImages";
	public static int DistanceRange=3000;
	public static String SalesPersonTodaysTargetMsg="";
	public static final String Preference="LTFoodsPrefrence";
	public static final String DistributorXMLFolder="LTFoodsDistributorXMLFolder";
	public static int flgAllRoutesData=1;
	public static String activityFrom="AllButtonActivity";

	public static String ActiveRouteSM="0";






*/










}
