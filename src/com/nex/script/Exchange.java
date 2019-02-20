package com.nex.script;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.nex.utils.json.JsonObject;
import com.nex.utils.json.JsonObject.Member;
import org.rspeer.script.Script;
import org.rspeer.ui.Log;

import javax.net.ssl.HttpsURLConnection;


public class Exchange {
	private final static String RSBUDDY_URL = "https://rsbuddy.com/exchange/summary.json";
	static String wikiUrl = "https://oldschool.runescape.wiki/w/Exchange:";
	private static Map<Integer, Integer> myCache = Collections.synchronizedMap(new WeakHashMap<Integer, Integer>());
	private static Map<Integer, String> names = Collections.synchronizedMap(new WeakHashMap<Integer, String>());

	public static String getName(int item) {
		if(item == 995) return "Coins";
		String name = null;
		if (names.containsKey(item)) {
			return names.get(item);
		}
		try {
			URL url = new URL(RSBUDDY_URL);
			BufferedReader jsonFile = new BufferedReader(new InputStreamReader(url.openStream()));

			JsonObject priceJSON = JsonObject.readFrom(jsonFile.readLine());
			Iterator<Member> iterator = priceJSON.iterator();

			while (iterator.hasNext()) {
				JsonObject itemJSON = priceJSON.get(iterator.next().getName()).asObject();
				int itemID = itemJSON.get("id").asInt();
				if (item == itemID) {
					name = itemJSON.get("name").asString();
					break;
				}
			}
			jsonFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		names.put(item, name);
		return name;
	}

	public static int getPrice(int item) {
		if(item == 995) return 0;
		int price = 0;
		if (myCache.containsKey(item)) {
			return myCache.get(item);
		}
		try {
			URL url = new URL(RSBUDDY_URL);
			Path filename = getRSBuddySummary();
			InputStream stream;
			if(filename != null)
				stream = Files.newInputStream(filename);
			else
				stream = url.openStream();
			BufferedReader jsonFile = new BufferedReader(new InputStreamReader(stream));

			JsonObject priceJSON = JsonObject.readFrom(jsonFile.readLine());
			Iterator<Member> iterator = priceJSON.iterator();

			while (iterator.hasNext()) {
				JsonObject itemJSON = priceJSON.get(iterator.next().getName()).asObject();
				int itemID = itemJSON.get("id").asInt();
				if (item == itemID) {
					String itemName = itemJSON.get("name").asString();
					names.put(item, itemName);
					price = itemJSON.get("buy_average").asInt();
					if (price == 0) {
						price = getRealPrice(itemName);
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myCache.put(item, price);
		return price;
	}
	static long getSecondsFromModification(File f) throws IOException {
		Path attribPath = f.toPath();
		BasicFileAttributes basicAttribs
				= Files.readAttributes(attribPath, BasicFileAttributes.class);
		return (System.currentTimeMillis()
				- basicAttribs.lastModifiedTime().to(TimeUnit.MILLISECONDS))
				/ 1000;
	}
	static String getSummaryJson(){
		return Script.getDataDirectory().toString() + "/summary.json";
	}
	private static Path getRSBuddySummary() {
		try {
			String filename = getSummaryJson();
			File file = new File(filename);
			try {
				if (file.exists() && getSecondsFromModification(file) < 20 * 60)
					return file.toPath();
			}catch (IOException ex) { }
			HttpsURLConnection con = (HttpsURLConnection)new URL(RSBUDDY_URL).openConnection();
			if(con.getResponseCode() != 200){
				Log.info("response code " + con.getResponseCode());
				return null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) con.getContent()));
			String out = reader.lines().collect(Collectors.joining());
			reader.close();
			con.disconnect();

			Path path = Paths.get(filename);
			try {
				Files.write(path, out.getBytes());
			}catch (IOException ex){}
			return path;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	

	private static int getRealPrice(String itemName) throws IOException {
		URL url = new URL(wikiUrl + itemName);
		HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
		httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
		InputStream is = httpcon.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		String price = "";
		while ((line = br.readLine()) != null) {
			if (line.contains("GEPrice")) {
				String sub = line.split("GEPrice\">")[1];
				price = sub.split("<")[0].replaceAll(",", "").replaceAll("\"", "");
			}
		}
		return (int) Double.parseDouble(price);
	}

}
