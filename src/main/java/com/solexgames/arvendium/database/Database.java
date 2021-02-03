package com.solexgames.arvendium.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.solexgames.arvendium.ArvendiumPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

@Getter
@Setter
public class Database {

    private MongoClient client;
    private MongoDatabase database;

    private MongoCollection<Document> ranks;
    private MongoCollection<Document> profiles;

    public Database() {
        this.client = new MongoClient(new MongoClientURI(ArvendiumPlugin.getInstance().getConfigFile().getString("DATABASE.MONGO.URI")));
        this.database = this.client.getDatabase("SolexGames");
        this.ranks = this.database.getCollection("ranks");
        this.profiles = this.database.getCollection("permprofiles");
    }
}
