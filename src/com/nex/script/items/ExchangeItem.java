package com.nex.script.items;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExchangeItem {

    private final String name;
    private final int id;
    private int overallAverage = -1;
    private int buyAverage = -1;
    private int sellAverage = -1;
    private int buyingQuantity;
    private int sellingQuantity;

    public ExchangeItem(final String name, final int id) {
        this.name = name;
        this.id = id;
        updateRSBuddyValues();
    }

    public final String getName() {
        return name;
    }

    public final int getId() {
        return id;
    }

    public final int getBuyAverage() {
        return buyAverage;
    }

    public final int getSellAverage() {
        return sellAverage;
    }

    public final int getBuyingQuantity() {
        return buyingQuantity;
    }

    public final int getSellingQuantity() {
        return sellingQuantity;
    }

    public void updateRSBuddyValues() {
        try {
            URL url = new URL("http://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + id);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
            con.setUseCaches(true);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json = br.readLine();
            br.close();

            getItemValue("overall", json).ifPresent(overallAverage -> this.overallAverage = overallAverage);

            getItemValue("buying", json).ifPresent(sellAverage -> this.sellAverage = sellAverage);

            getItemValue("selling", json).ifPresent(buyAverage -> this.buyAverage = buyAverage);

            getItemValue("buyingQuantity", json).ifPresent(buyQuantity -> this.buyingQuantity = buyQuantity);

            getItemValue("sellingQuantity", json).ifPresent(sellingQuantity -> this.sellingQuantity = sellingQuantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Optional<Integer> getItemValue(final String key, final String json) {
        Matcher overallAvgMatcher = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d*)").matcher(json);
        if (overallAvgMatcher.find()) {
            return Optional.of(Integer.parseInt(overallAvgMatcher.group(1)));
        }
        return Optional.empty();
    }

    public final String toString() {
        return String.format("Name: %s, ID: %d, Overall AVG: %d gp, Buying AVG: %d gp, Selling AVG: %d gp, Buying Quantity: %d, Selling Quantity:%d",
                name, id, overallAverage, buyAverage, sellAverage, buyingQuantity, sellingQuantity);
    }
}