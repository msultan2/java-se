package xml_parser;


import Directory_Operations.DirectoryReader;

import java.io.File;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.ListIterator;

import xml_parser.drs.*;

import org.w3c.dom.Document;
import org.w3c.dom.*;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XML_Parser_Class {
    public MI mi = new MI();

    public XML_Parser_Class(String iFileName, String oFileName) {
        iFilePath = iFileName;

        //XML_Parser_Class xmlParser = new XML_Parser_Class(iFileName,oFileName);
        
        initFile(iFilePath);

        String newFilePath = new String();
        String dir = new String();
        String pathSeparator = new File(System.getProperty("user.dir")).separator;
        
        newFilePath = iFilePath.substring(iFilePath.lastIndexOf(pathSeparator) + 1);
        dir = iFilePath.substring(0, iFilePath.lastIndexOf(pathSeparator) + 1);

        DirectoryReader dirObj = new DirectoryReader();
        dirObj.dirExists(dir + "Processing");

        Document doc = 
            formatToDocument(dir + "Processing"+pathSeparator + newFilePath + ".Proccessing");

        if (doc != null) {
            //System.out.println("Root element of the doc is " + 
            //                   doc.getDocumentElement().getNodeName());

            mi = getMI(doc);

            //Creating Header
            String headder = new String();
            headder = "Date_,FromTime,ToTime,Proccessor,Value";
            Iterator li = mi.getMt().iterator();
            while (li.hasNext()) {
                headder = headder + "," + (String)li.next();
            }
            //Getting Time And Date

            TimeDate timeDateObj = new TimeDate();
            timeDateObj = getTimeDate(newFilePath);

            mi.setTimeDate(timeDateObj);
            mi.setHeadder(headder);
            dirObj.deleteDir(new File(dir + "Processing"));
        }
    }
    private static String iFilePath = new String();
    private static String oFilePath = new String();

    private Document formatToDocument(String fileName) {
        Document doc = null;
        try {
            DocumentBuilderFactory docBuilderFactory = 
                DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = 
                docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(new File(fileName));

            // normalize text representation
            doc.getDocumentElement().normalize();
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + 
                               err.getLineNumber() + ", uri " + 
                               err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
            //      
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return doc;
    }

    private void initFile(String iFilePath) {

        File f = new File(iFilePath);
        if (!f.exists()) {
            System.out.println("File not found");
            System.exit(0);
        } else {
            CopyTextFile CopyTextFileObj = new CopyTextFile();
            String newFilePath = new String();
            String dir = new String();
            String pathSeparator = new File(iFilePath).separator;
            newFilePath = iFilePath.substring(iFilePath.lastIndexOf(pathSeparator) + 1);
            dir = iFilePath.substring(0, iFilePath.lastIndexOf(pathSeparator) + 1);

            DirectoryReader dirObj = new DirectoryReader();
            dirObj.dirExists(dir + "Processing");
            dirObj.dirExists(dir + "Received");
            CopyTextFileObj.copyFileWithoutString(iFilePath, 
                                                  dir + "Processing"+pathSeparator + 
                                                  newFilePath + ".Proccessing", 
                                                  "MeasDataCollection.dtd");
            f.renameTo(new File(dir + "Received"+pathSeparator + newFilePath));
        }
        //f.open();
        //remove 2nd line
        //f.close();
    }

    private NodeList xmlElements(Document doc, String tagName) {
        NodeList node = doc.getElementsByTagName(tagName);
        return node;
    }

    private ArrayList getElements(Document doc, String tagName) {
        NodeList listOfElements = 
            xmlElements(doc, doc.getDocumentElement().getNodeName());
        ArrayList listofElements_return = new ArrayList();
        for (int s = 0; s < listOfElements.getLength(); s++) {
            String processorString = new String();
            Node firstElementNode = listOfElements.item(s);
            if (firstElementNode.getNodeType() == Node.ELEMENT_NODE) {


                Element firstElement = (Element)firstElementNode;

                NodeList elementList = 
                    firstElement.getElementsByTagName(tagName);
                for (int j = 0; j < elementList.getLength(); j++) {
                    Element element = (Element)elementList.item(j);

                    NodeList textList = element.getChildNodes();

                    processorString = 
                            ((Node)textList.item(0)).getNodeValue().trim();
                    //System.out.println(tagName+": "+processorString);
                    listofElements_return.add(processorString);
                }
            }
        }
        return listofElements_return;
    }

    private MI getMI(Document doc) {
        ArrayList mtsList = new ArrayList();
        ArrayList gpList = new ArrayList();
        ArrayList mtList = new ArrayList();
        ArrayList mvList = new ArrayList();

        MI mi = new MI();

        mvList = getMVs(doc);
        mtsList = getElements(doc, "mts");
        gpList = getElements(doc, "gp");
        mtList = getElements(doc, "mt");

        mi.setGp(gpList.toString());
        mi.setMt(mtList);
        mi.setMts(mtsList.toString());
        mi.setMv(mvList);

        return mi;
    }

    private ArrayList getMVs(Document doc) {

        ArrayList MVList = new ArrayList();

        NodeList listOfMVs = xmlElements(doc, "mv");
        int totalMVs = listOfMVs.getLength();
        //System.out.println("Total no Processors : " + totalMVs);

        for (int s = 0; s < listOfMVs.getLength(); s++) {

            MV mvObj = new MV();

            Node firstMVNode = listOfMVs.item(s);
            if (firstMVNode.getNodeType() == Node.ELEMENT_NODE) {


                Element firstMVElement = (Element)firstMVNode;

                //------- set moid
                NodeList moidList = 
                    firstMVElement.getElementsByTagName("moid");
                Element moidElement = (Element)moidList.item(0);

                NodeList textMoidList = moidElement.getChildNodes();

                String processorString = new String();
                String CounterString = new String();
                processorString = 
                        ((Node)textMoidList.item(0)).getNodeValue().trim();
                int sourceLocation = 0;
                int counterLocation1 = 0;
//                int counterLocation2 = 0;
                sourceLocation = 
                        processorString.indexOf("Source =") + (new String("Source =")).length();
                counterLocation1 = 
                        processorString.indexOf("=") + (new String("=")).length();
//                counterLocation2 = 
//                        processorString.indexOf(",") + (new String(",")).length();
                CounterString = 
                        processorString.substring(counterLocation1, sourceLocation - 
                                                  10).trim();
                processorString = 
                        processorString.substring(sourceLocation).trim();
                mvObj.setMoid(processorString);
                mvObj.setCounter(CounterString);
                //System.out.println("moid : " + mvObj.getMoid());

                //------- set Rs
                ArrayList rList = new ArrayList();
                NodeList value = firstMVElement.getElementsByTagName("r");
                int i = 0;
                while (i < value.getLength()) {
                    Element rElement = (Element)value.item(i);

                    NodeList textRList = rElement.getChildNodes();
                    if (textRList.getLength() != 0) {
                        rList.add(((Node)textRList.item(0)).getNodeValue().trim());
                        //System.out.println("r : " + 
                        //                   ((Node)textRList.item(0)).getNodeValue().trim());
                    } else {
                        rList.add("0");
                        //System.out.println("r : 0");
                    }
                    i++;
                }
                mvObj.setR(rList);

                //---- set sf
                NodeList ageList = firstMVElement.getElementsByTagName("sf");
                Element ageElement = (Element)ageList.item(0);

                NodeList textAgeList = ageElement.getChildNodes();
                mvObj.setSf(((Node)textAgeList.item(0)).getNodeValue().trim());
                //System.out.println("sf : " + (String)mvObj.getSf());
            }
            MVList.add(mvObj);
        }
        return MVList;
    }

    private TimeDate getTimeDate(String fileName) {
        if (fileName.length() > 20) {
            TimeDate timeDateObj = new TimeDate();
            timeDateObj.setDate(fileName.substring(1, 9));
            timeDateObj.setFromtime(fileName.substring(10, 14));
            timeDateObj.setTotime(fileName.substring(15, 19));
            return timeDateObj;
        } else {
            return null;
        }
    }

    public static void main(String[] argv) {


        XML_Parser_Class xmlParser = 
            new XML_Parser_Class("SSS.xml", "D:\\Temp\\X.txt");

        xmlParser.initFile(iFilePath);

        Document doc = xmlParser.formatToDocument(iFilePath + ".Proccessing");

        if (doc != null) {
            //System.out.println("Root element of the doc is " + 
            //                   doc.getDocumentElement().getNodeName());

            MI mi = new MI();
            mi = xmlParser.getMI(doc);
            CopyTextFile CopyTextFileObj = new CopyTextFile();

            //Creating Header
            String headder = new String();
            headder = "TimeStamp,Counter,Source";
            Iterator li = mi.getMt().iterator();
            while (li.hasNext()) {
                headder = headder + "," + (String)li.next();
            }
            //Getting Time And Date

            TimeDate timeDateObj = new TimeDate();
            timeDateObj = xmlParser.getTimeDate(iFilePath);

            mi.setTimeDate(timeDateObj);
            mi.setHeadder(headder);


            //Filling Headder
            CopyTextFileObj.fileInsertText(oFilePath, headder, false);

            //Adding Data
            MV tempMV = new MV();
            String values = new String();
            li = mi.getMv().iterator();
            while (li.hasNext()) {
                //headder = headder + "," + (String)li.next();
                tempMV = (MV)li.next();
                Iterator lr = tempMV.getR().iterator();
                values = "";
                while (lr.hasNext()) {
                    if (values.length() == 0)
                        values = (String)lr.next();
                    else
                        values = values + "," + (String)lr.next();
                }
                //System.out.println (tempMV.getMoid()+","+tempMV.getR());
                CopyTextFileObj.fileInsertText(oFilePath, 
                                               timeDateObj.getDate() + "." + 
                                               timeDateObj.getFromtime() + 
                                               "-" + timeDateObj.getTotime() + 
                                               "," + tempMV.getMoid() + "," + 
                                               values, true);
            }

        }
    }
}
