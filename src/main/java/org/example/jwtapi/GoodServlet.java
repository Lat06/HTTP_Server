package org.example.jwtapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/api/good/*")
public class GoodServlet extends HttpServlet {
    private static final Map<Integer, Map<String, Object>> goods = new ConcurrentHashMap<>();
    private static int counter = 1;
    private final Gson gson = new Gson();
    private final SecretKey secretKey = Keys.hmacShaKeyFor("MySuperSecretKeyMySuperSecretKey".getBytes(StandardCharsets.UTF_8)); // 32-byte key

    private boolean isAuthorized(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String subject = claims.getSubject();
            return subject != null && !subject.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAuthorized(req)) {
            res.sendError(403, "Missing or invalid token");
            return;
        }

        String path = req.getPathInfo(); // /{id}
        if (path == null || path.length() <= 1) {
            res.sendError(404);
            return;
        }

        int id = Integer.parseInt(path.substring(1));
        Map<String, Object> good = goods.get(id);
        if (good == null) {
            res.sendError(404);
            return;
        }

        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(good));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAuthorized(req)) {
            res.sendError(403, "Missing or invalid token");
            return;
        }

        JsonObject json = JsonParser.parseReader(req.getReader()).getAsJsonObject();
        if (!json.has("name") || !json.has("price") || !json.has("category")) {
            res.sendError(409, "Missing fields");
            return;
        }

        double price = json.get("price").getAsDouble();
        if (price < 0) {
            res.sendError(409, "Invalid price");
            return;
        }

        int id = counter++;
        Map<String, Object> good = Map.of(
                "id", id,
                "name", json.get("name").getAsString(),
                "price", price,
                "category", json.get("category").getAsString()
        );
        goods.put(id, good);

        res.setStatus(201);
        res.setContentType("application/json");
        res.getWriter().write(gson.toJson(Map.of("id", id)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAuthorized(req)) {
            res.sendError(403, "Missing or invalid token");
            return;
        }

        String path = req.getPathInfo(); // /{id}
        if (path == null || path.length() <= 1) {
            res.sendError(404);
            return;
        }

        int id = Integer.parseInt(path.substring(1));
        if (!goods.containsKey(id)) {
            res.sendError(404);
            return;
        }

        JsonObject json = JsonParser.parseReader(req.getReader()).getAsJsonObject();
        double price = json.has("price") ? json.get("price").getAsDouble() : 0;
        if (json.has("price") && price < 0) {
            res.sendError(409, "Invalid price");
            return;
        }

        Map<String, Object> oldGood = goods.get(id);
        Map<String, Object> updated = Map.of(
                "id", id,
                "name", json.has("name") ? json.get("name").getAsString() : oldGood.get("name"),
                "price", json.has("price") ? price : oldGood.get("price"),
                "category", json.has("category") ? json.get("category").getAsString() : oldGood.get("category")
        );

        goods.put(id, updated);
        res.setStatus(204);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAuthorized(req)) {
            res.sendError(403, "Missing or invalid token");
            return;
        }

        String path = req.getPathInfo(); // /{id}
        if (path == null || path.length() <= 1) {
            res.sendError(404);
            return;
        }

        int id = Integer.parseInt(path.substring(1));
        if (!goods.containsKey(id)) {
            res.sendError(404);
            return;
        }

        goods.remove(id);
        res.setStatus(204);
    }
}
