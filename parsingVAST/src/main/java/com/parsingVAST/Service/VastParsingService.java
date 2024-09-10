package com.parsingVAST.Service;


import com.parsingVAST.Model.VastData;
import com.parsingVAST.Model.Creative;
import com.parsingVAST.Model.CompanionBanner;
import com.parsingVAST.Model.Impression;
import com.parsingVAST.Repository.VastDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class VastParsingService {

    @Autowired
    private VastDataRepository vastDataRepository;

    // Method to read XML from a file
    public String readXmlFromFile(String filePath) {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            StringBuilder xmlContent = new StringBuilder();
            while (scanner.hasNextLine()) {
                xmlContent.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            return xmlContent.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to read XML from a URL
    public String readXmlFromUrl(String url) {
        try {
            URL vastUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) vastUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            Scanner scanner = new Scanner(vastUrl.openStream());
            StringBuilder xmlContent = new StringBuilder();
            while (scanner.hasNextLine()) {
                xmlContent.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            return xmlContent.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to parse the XML content into VastData object
    public VastData parseVastXml(String xmlContent) {
        try {
            // Initialize XML parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));
            
            VastData vastData = new VastData();
            
            // Parse VAST tag version
            Element vastElement = document.getDocumentElement();
            vastData.setVersion(vastElement.getAttribute("version"));

            // Parse ID, title, description
            Element adElement = (Element) document.getElementsByTagName("Ad").item(0);
            vastData.setId(adElement.getAttribute("id"));

            Element inLineElement = (Element) document.getElementsByTagName("InLine").item(0);
            vastData.setTitle(inLineElement.getElementsByTagName("AdTitle").item(0).getTextContent());
            vastData.setDescription(inLineElement.getElementsByTagName("Description").item(0).getTextContent());

            // Parse Impression
            Element impressionElement = (Element) document.getElementsByTagName("Impression").item(0);
            Impression impression = new Impression();
            impression.setId(impressionElement.getAttribute("id"));
            impression.setUrl(impressionElement.getTextContent());
            vastData.setImpression(impression);

            // Parse Creatives
            List<Creative> creativeList = new ArrayList<>();
            NodeList creatives = document.getElementsByTagName("Creative");

            for (int i = 0; i < creatives.getLength(); i++) {
                Creative creative = new Creative();

                // Parse duration
                Element linearElement = (Element) ((Element) creatives.item(i)).getElementsByTagName("Linear").item(0);
                creative.setDuration(linearElement.getElementsByTagName("Duration").item(0).getTextContent());

                // Parse companion banners
                NodeList companionAds = linearElement.getElementsByTagName("Companion");
                List<CompanionBanner> companionBanners = new ArrayList<>();
                for (int j = 0; j < companionAds.getLength(); j++) {
                    CompanionBanner banner = new CompanionBanner();
                    Element bannerElement = (Element) companionAds.item(j);
                    banner.setId(bannerElement.getAttribute("id"));
                    banner.setWidth(Integer.parseInt(bannerElement.getAttribute("width")));
                    banner.setHeight(Integer.parseInt(bannerElement.getAttribute("height")));
                    banner.setType(bannerElement.getAttribute("type"));
                    banner.setSource(bannerElement.getElementsByTagName("StaticResource").item(0).getTextContent());
                    banner.setClickThroughUrl(bannerElement.getElementsByTagName("CompanionClickThrough").item(0).getTextContent());
                    companionBanners.add(banner);
                }
                creative.setCompanionBanners(companionBanners);

                // Parse media files, tracking events, video clicks similarly (skipped for brevity)

                creativeList.add(creative);
            }
            vastData.setCreatives(creativeList);

            return vastData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to export VastData object to JSON
    public String exportToJson(VastData vastData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(vastData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to save VastData object to MongoDB
    public VastData saveVastData(VastData vastData) {
        return vastDataRepository.save(vastData);
    }

    // Method to query VastData object by ID from MongoDB
    public VastData getVastDataById(String id) {
        return vastDataRepository.findById(id).orElse(null);
    }

    // Method to parse and save VAST data from a URL
    public void parseAndSaveFromUrl(String url) {
        String xmlContent = readXmlFromUrl(url);
        if (xmlContent != null) {
            VastData vastData = parseVastXml(xmlContent);
            if (vastData != null) {
                saveVastData(vastData);
            }
        }
    }

    // Method to parse and save VAST data from a file
    public void parseAndSaveFromFile(String filePath) {
        String xmlContent = readXmlFromFile(filePath);
        if (xmlContent != null) {
            VastData vastData = parseVastXml(xmlContent);
            if (vastData != null) {
                saveVastData(vastData);
            }
        }
    }

    // Method to get all VAST data from MongoDB
    public List<VastData> getAllVastData() {
        return vastDataRepository.findAll();
    }
}

