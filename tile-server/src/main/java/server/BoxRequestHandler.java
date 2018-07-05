package server;

import box.BoxandData;
import box.History;
import box.MikeBoxGetter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.Config;
import main.Main;
import project.Canvas;
import project.Project;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BoxRequestHandler  implements HttpHandler {

    // gson builder
    private final Gson gson;
    private final Project project;
    private MikeBoxGetter boxGetter;
    private History history;

    public BoxRequestHandler() {

        gson = new GsonBuilder().create();
        project = Main.getProject();
        boxGetter = new MikeBoxGetter();

    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        // TODO: this method should be thread safe, allowing concurrent requests
        System.out.println("\nServing /dynamic Box");

        // get data of the current request
        // variable definitions
        String response;
        String canvasId;
        int cx, cy;
        int viewportH, viewportW;
        String predicate;
        BoxandData data = null;

        // check if this is a POST request
        if (! httpExchange.getRequestMethod().equalsIgnoreCase("POST")) {
            Server.sendResponse(httpExchange, HttpsURLConnection.HTTP_BAD_METHOD, "");
            return;
        }

        // get data of the current request
        InputStreamReader isr =  new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        Map<String, String> queryMap = Server.queryToMap(query);
        // print
        for (String s : queryMap.keySet())
            System.out.println(s + " : " + queryMap.get(s));

        // check parameters, if not pass, send a bad request response
        response = checkParameters(queryMap);
        if (response.length() > 0) {
            Server.sendResponse(httpExchange, HttpsURLConnection.HTTP_BAD_REQUEST, response);
            return;
        }

        // get parameters
        canvasId = queryMap.get("id");
        cx = Integer.valueOf(queryMap.get("x"));
        cy = Integer.valueOf(queryMap.get("y"));
        viewportH = Integer.valueOf(queryMap.get("viewportH"));
        viewportW = Integer.valueOf(queryMap.get("viewportW"));
        Canvas c = project.getCanvas(canvasId);
        ArrayList<String> predicates = new ArrayList<>();

        for (int i = 0; i < c.getLayers().size(); i ++)
            predicates.add(queryMap.get("predicate" + i));

        //get box data
        try {
            data = boxGetter.getBox(c, cx, cy, viewportH, viewportW, predicates, project);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //send data and box back
        Map<String, Object> respMap = new HashMap<>();
        respMap.put("renderData", data);
    //    respMap.put("minx", minx);
    //    respMap.put("miny", miny);
        response = gson.toJson(respMap);

        // send back response
        Server.sendResponse(httpExchange, HttpsURLConnection.HTTP_OK, response);
        System.out.println();
    }
    private String checkParameters(Map<String, String> queryMap) {

        // check fields
        if (! queryMap.containsKey("id"))
            return "canvas id missing.";
        if (! queryMap.containsKey("x") || ! queryMap.containsKey("y"))
            return "x or y missing.";

        String canvasId = queryMap.get("id");
        int minx = Integer.valueOf(queryMap.get("x"));
        int miny = Integer.valueOf(queryMap.get("y"));

        // check whether this canvas exists
        if (project.getCanvas(canvasId) == null)
            return "Canvas " + canvasId + " does not exist!";

        // check whether x and y corresponds to the top-left corner of a tile
        if (minx % Config.tileW != 0 || miny % Config.tileH != 0)
            return "x and y must be a multiple of tile size!";

        // check passed
        return "";
    }
}