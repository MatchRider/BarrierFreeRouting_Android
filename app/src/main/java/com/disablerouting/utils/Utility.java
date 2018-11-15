package com.disablerouting.utils;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.disablerouting.api.ApiEndPoint;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.model.NodeReference;
import com.disablerouting.login.OSMApi;
import com.disablerouting.osm_activity.model.GetOSM;
import com.disablerouting.osm_activity.model.GetOsmData;
import com.disablerouting.route_planner.model.Way;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth10aService;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.overlay.OverlayItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utility {
    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(AppCompatActivity activity) {
        if (activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static TranslateAnimation translate(float fromX, float toX, float fromY,
                                               float toY, int ms) {
        TranslateAnimation transAnim = new TranslateAnimation(fromX, toX, fromY, toY);
        transAnim.setDuration(ms);
        return transAnim;
    }

    public static int calculatePopUpHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static BoundingBox boundToMap(double minLatitude, double maxLatitude,
                                         double minLongitude, double maxLongitude) {
        double minLat = minLatitude;
        double maxLat = maxLatitude;
        double minLong = minLongitude;
        double maxLong = maxLongitude;
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        for (OverlayItem item : items) {
            IGeoPoint point = item.getPoint();
            if (point.getLatitudeE6() < minLat)
                minLat = point.getLatitudeE6();
            if (point.getLatitudeE6() > maxLat)
                maxLat = point.getLatitudeE6();
            if (point.getLongitudeE6() < minLong)
                minLong = point.getLongitudeE6();
            if (point.getLongitudeE6() > maxLong)
                maxLong = point.getLongitudeE6();
        }

        return new BoundingBox(maxLat, maxLong, minLat, minLong);
    }

    public static String trimTWoDecimalPlaces(double value) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(value);
    }

    public static JSONObject convertXMLtoJSON(String xmlString) {
        JSONObject jsonObject = null;
        try {

            XmlToJson xmlToJson = new XmlToJson.Builder(xmlString).build();
            jsonObject = xmlToJson.toJson();
            if (jsonObject != null) {
                Log.d("JSON", jsonObject.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static OAuth10aService createOauth10a() {
        return new ServiceBuilder(ApiEndPoint.CONSUMER_KEY)
                .apiSecret(ApiEndPoint.CONSUMER_SECRET_KEY)
                .callback(ApiEndPoint.OSM_REDIRECT_URI)
                .build(OSMApi.instance());
    }

    public static void expand(View view) {
        //set Visible
        view.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, view.getMeasuredHeight(), view);
        mAnimator.setDuration(1000).start();
    }

    public static ValueAnimator slideAnimator(int start, int end, final View view) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }


    public static void collapse(final View view) {
        int finalHeight = view.getHeight();
        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, view);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }

    public static String readOSMFile(Context context) {
        InputStream input;
        try {
            input = context.getAssets().open("dev_map.osm");
            Reader reader = new InputStreamReader(input);
            StringBuilder sb = new StringBuilder();
            char buffer[] = new char[16384];  // read 16k blocks
            int len;
            while ((len = reader.read(buffer)) > 0) {
                sb.append(buffer, 0, len);
            }
            reader.close();
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isParsableAsDouble(final String s) {
        try {
            if (s != null) {
                Double.valueOf(s);
            }
            return true;
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    public static void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                    startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static String changeCmToMeter(String stringUnitPassed) {
        return String.valueOf(Double.parseDouble(stringUnitPassed) / 100);
    }

    public static String covertValueRequired(String stringUnitPassed) {
        String converted = stringUnitPassed;
        if (stringUnitPassed.contains(",")) {
            converted = converted.replace(",", ".");
        }
        if (stringUnitPassed.contains(">")) {
            converted = converted.replace(">", "");
        }
        if (stringUnitPassed.contains("<")) {
            converted = converted.replace("<", "");
        }
        if (stringUnitPassed.contains("unter")) {
            converted = converted.replace("unter", "");
        }
        if (stringUnitPassed.contains("über")) {
            converted = converted.replace("über", "");
        }
        if (stringUnitPassed.contains("bis")) {
            converted = converted.replace("bis", "");
        }
        if (stringUnitPassed.contains("kein Bordstein")) {
            converted = converted.replace("kein Bordstein", "0");
        }
        if (stringUnitPassed.contains("keine Steigung")) {
            converted = converted.replace("keine Steigung", "0");
        }
        if (stringUnitPassed.contains("No curb")) {
            converted = converted.replace("No curb", "0");
        }
        if (stringUnitPassed.contains("No gradient")) {
            converted = converted.replace("No gradient", "0");
        }
        if (stringUnitPassed.contains("%")) {
            converted = converted.replace("%", "");
        }
        if (stringUnitPassed.contains("m")) {
            converted = converted.replace("m", "");
        }
        if (stringUnitPassed.contains("cm")) {
            converted = converted.replace("cm", "");
        }
        if (stringUnitPassed.contains("inches")) {
            converted = converted.replace("inches", "");
        }
        if (stringUnitPassed.contains("above")) {
            converted = converted.replace("above", "");
        }
        if (stringUnitPassed.contains("below")) {
            converted = converted.replace("below", "");
        }
        return converted;
    }

    public static String changeDotToComma(String stringUnitPassed) {
        String converted = stringUnitPassed;
        if (stringUnitPassed.contains(".")) {
            converted = stringUnitPassed.replace(".", ",");
        }
        return converted;
    }

    public static String changeMeterToCm(String stringUnitPassed) {
        String converted = stringUnitPassed;
        if (stringUnitPassed != null && stringUnitPassed.contains(".")) {
            converted = String.valueOf(Double.parseDouble(stringUnitPassed) * 100);
        }
        return converted;
    }

    public static boolean isListContainId(List<NodeReference> nodeReferencesList, String id) {
        for (NodeReference nodeReference : nodeReferencesList) {
            if (nodeReference.getOSMNodeId().contains(id)) {
                return true;
            }
        }
        return false;
    }


    public static void getElementValues(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0, len = nodeList.getLength(); i < len; i++) {
            Node currentNode = nodeList.item(i);
            if (len == 1 && currentNode.getNodeType() == Node.TEXT_NODE) {
                System.out.println(node.getLocalName() + "=" + currentNode.getTextContent());
            } else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                getElementValues(currentNode);
            }
        }
    }

    private static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    private static String nodeListToStringWay(NodeList nodes) throws TransformerException {
        StringBuilder result = new StringBuilder();
        int len = nodes.getLength();
        for (int i = 0; i < len; ++i) {
            Node node = nodes.item(i);
            Element eElement = (Element) node;
            NodeList cElementTag = eElement.getElementsByTagName("tag");

            boolean isHaveSeparateGeometry = false;
            boolean isHaveKeyHighway = false;
            boolean isHaveKeyFootWay = false;
            boolean isSideWalkPartOfWay = false;
            boolean isSideWalkPartOfWayNOKey = false;

            for (int j = 0; j < cElementTag.getLength(); j++) {

                String key = cElementTag.item(j).getAttributes().getNamedItem("k").getNodeValue();
                String value = cElementTag.item(j).getAttributes().getNamedItem("v").getNodeValue();
                if (key.equalsIgnoreCase(AppConstant.KEY_HIGHWAY) && value.equalsIgnoreCase(AppConstant.KEY_FOOTWAY)) {
                    isHaveKeyHighway = true;
                }
                if (key.equalsIgnoreCase(AppConstant.KEY_FOOTWAY) && value.equalsIgnoreCase(AppConstant.KEY_SIDEWALK)) {
                    isHaveKeyFootWay = true;
                }
                if (isHaveKeyHighway && isHaveKeyFootWay) {
                    isHaveSeparateGeometry = true;
                }
                if (key.equalsIgnoreCase(AppConstant.KEY_SIDEWALK)) {
                    isSideWalkPartOfWay = true;
                }
                if (key.equalsIgnoreCase(AppConstant.KEY_SIDEWALK) && value.equalsIgnoreCase("NO")) {
                    isSideWalkPartOfWayNOKey = true;
                }

            }
            if ((isHaveSeparateGeometry || isSideWalkPartOfWay) && !isSideWalkPartOfWayNOKey) {
                result.append(nodeToString(nodes.item(i)));
            }

        }
        return result.toString();
    }
    private static String nodeListToStringNode(NodeList nodes, List<Way> listWay) throws TransformerException {
        StringBuilder result = new StringBuilder();
        int len = nodes.getLength();
        int lenWay = listWay.size();

        //FOR 311 items
        for (int i = 0; i < lenWay; i++) {
            for (int j=0;j< listWay.get(i).getNdList().size();j++){
                String nodeID=listWay.get(i).getNdList().get(j).getRef();
                for (int k = 0; k < len; k++) { // NODES iterate 45000
                    Node node = nodes.item(k);
                    if(node.getAttributes().getNamedItem("id").getNodeValue().equalsIgnoreCase(nodeID)){
                        result.append(nodeToString(nodes.item(k)));
                        break;
                    }
                }
            }
        }

        return result.toString();
    }

    public static GetOsmData convertDataIntoModel(String data) throws IOException {
        GetOsmData osmData = new GetOsmData();
        GetOSM getOSM;
        GetOSM getOSMNode;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(data));
            Document doc = builder.parse(src);
            if (doc != null) {
                Element root = doc.getDocumentElement();
                String wayData;
                String nodeData;
                NodeList wayNodeList = root.getElementsByTagName("way");
                NodeList nodeNodeList = root.getElementsByTagName("node");
                try {
                    wayData = nodeListToStringWay(wayNodeList);
                    JSONObject jsonObjectWay = Utility.convertXMLtoJSON(wayData);

                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                        getOSM = objectMapper.readValue(jsonObjectWay.toString(), GetOSM.class); // GET WAYS first

                        nodeData = nodeListToStringNode(nodeNodeList, getOSM.getWays());
                        JSONObject jsonObjectNode = Utility.convertXMLtoJSON(nodeData);

                        getOSMNode = objectMapper.readValue(jsonObjectNode.toString(), GetOSM.class); //Get Nodes

                        getOSM.setNode(getOSMNode.getNode());
                        osmData.setOSM(getOSM);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        return osmData;
    }

    public static String randomColor() {
        Random rnd = new Random();
        int intColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return String.format("#%06X", (0xFFFFFF & intColor));
    }

    public static String getAppLanguage(){
        return Locale.getDefault().getDisplayLanguage();
    }

}

