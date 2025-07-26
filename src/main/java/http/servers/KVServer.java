package main.java.http.servers;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class KVServer {
	public static final int PORT = 8078;
	private final String apiToken;
	private final HttpServer server;
	private Map<String, String> data = new HashMap<>();
	private final Path dataFile = Paths.get("kvserver_data.txt");
	private final Gson gson;

	public KVServer() throws IOException {
		gson = new Gson();
		apiToken = generateApiToken();
		server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
		server.createContext("/register", this::register);
		server.createContext("/save", this::save);
		server.createContext("/load", this::load);
		server.createContext("/", this::hello);
		loadDataFromDisk();
	}

	private void hello(HttpExchange h) throws IOException{
		String response = "Port: 8078\n Token: http://localhost:8078/registry";
		h.sendResponseHeaders(200, 0);

		try (OutputStream os = h.getResponseBody()){
			os.write(response.getBytes());
		}
	}

	private void load(HttpExchange h) {
		try {
			System.out.println("\n/load");
			if (!hasAuth(h)) {
				System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
				h.sendResponseHeaders(403, 0);
				return;
			}
			if ("GET".equals(h.getRequestMethod())) {
				String key = h.getRequestURI().getPath().substring("/load/".length());
				if (key.isEmpty()) {
					System.out.println("Key для загрузки пустой. key указывается в пути: /load/{key}");
					h.sendResponseHeaders(400, 0);
					return;
				}
				String value = data.get(key);
				if (value != null) {
					sendText(h, value);
					System.out.println("Значение для ключа " + key + " успешно отправлено");
				} else {
					System.out.println("Значение для ключа " + key + " не найдено");
					h.sendResponseHeaders(404, 0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			h.close();
		}
	}

	private void save(HttpExchange h) throws IOException {
		try {
			System.out.println("\n/save");
			if (!hasAuth(h)) {
				System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
				h.sendResponseHeaders(403, 0);
				return;
			}
			if ("POST".equals(h.getRequestMethod())) {
				String key = h.getRequestURI().getPath().substring("/save/".length());
				if (key.isEmpty()) {
					System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
					h.sendResponseHeaders(400, 0);
					return;
				}
				String value = readText(h);
				if (value.isEmpty()) {
					System.out.println("Value для сохранения пустой. value указывается в теле запроса");
					h.sendResponseHeaders(400, 0);
					return;
				}
				data.put(key, value);
				System.out.println("Значение для ключа " + key + " успешно обновлено!");
				h.sendResponseHeaders(200, 0);
			} else {
				System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
				h.sendResponseHeaders(405, 0);
			}
		} finally {
			h.close();
		}
	}

	private void register(HttpExchange h) throws IOException {
		try {
			System.out.println("\n/register");
			if ("GET".equals(h.getRequestMethod())) {
				sendText(h, apiToken);
			} else {
				System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
				h.sendResponseHeaders(405, 0);
			}
		} finally {
			h.close();
		}
	}

	public void start() {
		System.out.println("Запускаем сервер на порту " + PORT);
		System.out.println("Открой в браузере http://localhost:" + PORT + "/");
		System.out.println("API-KEY: " + apiToken);
		server.start();
	}

	private void loadDataFromDisk() {
		if (Files.exists(dataFile)) {
			try {
				String content = Files.readString(dataFile);
				deserializeData(content);
			} catch (IOException e) {
				System.err.println("Ошибка загрузки данных: " + e.getMessage());
				data = new HashMap<>();
			}
		} else {
			data = new HashMap<>();
		}
	}

	private void saveDataToDisk() {
		try {
			Path parentDir = dataFile.getParent();
			if (parentDir != null)
				Files.createDirectories(parentDir);

			String jsonData = serializeData();

			System.out.println("Сохранение данных в файл: " + dataFile.toAbsolutePath());
			System.out.println("Размер данных: " + jsonData.length() + " символов");

			Files.writeString(
					dataFile,
					jsonData,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE
			);

			System.out.println("Данные успешно сохранены");
		} catch (IOException e) {
			System.err.println("Ошибка сохранения данных: " + e.getMessage());
		}
	}

	public void stop() {
		saveDataToDisk();
		server.stop(0);
		System.out.println("Сервер остановлен, данные сохранены в " + dataFile.toAbsolutePath());
	}

	private String serializeData() {
		return gson.toJson(data);
	}

	private void deserializeData(String json) {
		if (json == null || json.isEmpty()) {
			System.out.println("Нет данных для загрузки, создана новая пустая карта");
			data = new HashMap<>();
			return;
		}

		try {
			Type type = new TypeToken<Map<String, String>>(){}.getType();
			data = gson.fromJson(json, type);
		} catch (JsonSyntaxException e) {
			System.err.println("Ошибка десериализации данных: " + e.getMessage());
			data = new HashMap<>();
		}
	}

	private String generateApiToken() {
		return "" + System.currentTimeMillis();
	}

	protected boolean hasAuth(HttpExchange h) {
		String rawQuery = h.getRequestURI().getRawQuery();
		return rawQuery != null && (rawQuery.contains("API-KEY=" + apiToken) || rawQuery.contains("API-KEY=DEBUG"));
	}

	protected String readText(HttpExchange h) throws IOException {
		return new String(h.getRequestBody().readAllBytes(), UTF_8);
	}

	protected void sendText(HttpExchange h, String text) throws IOException {
		byte[] resp = text.getBytes(UTF_8);
		h.getResponseHeaders().add("Content-Type", "application/json");
		h.sendResponseHeaders(200, resp.length);
		h.getResponseBody().write(resp);
	}
}
