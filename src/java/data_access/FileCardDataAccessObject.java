package data_access;

import Database.DatabaseHelper;
import Entities.Card;
import Entities.Stats;
import Entities.User;
import use_case.StatsGallery.StatsGalleryDataAccessInterface;
import use_case.gallery.GalleryUserDataAccessInterface;
import use_case.login.LoginUserDataAcesssInterface;
import use_case.lootbox.LootboxUserDataAccessInterface;
import use_case.signup.SignupUserDataAcesssInterface;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileCardDataAccessObject implements LootboxUserDataAccessInterface, GalleryUserDataAccessInterface, StatsGalleryDataAccessInterface, SignupUserDataAcesssInterface, LoginUserDataAcesssInterface {

    private final ArrayList<Integer> CardArray = new ArrayList<>();

    private final Map<Integer, Card> Cards = new HashMap<>();

    private final File CardInfo;

    private final File Cardlist;

    private final DatabaseHelper databaseHelper;

    private User Commonuser;

    public FileCardDataAccessObject(String csvPath, String dbPath) throws IOException {

        CardInfo = new File(csvPath);
        Cardlist = new File(csvPath);

        this.databaseHelper = new DatabaseHelper(dbPath);


        loadallcards();

    }

//    public static String serialize(Card card) {
//        return card.getId() + "|" +
//                card.getName() + "|" +
//                card.getImageID() + "|" +
//                card.getimgpath() + "|" +
//                card.getStats().serializer() + "|" +
////                card.getAttackStatOptions().stream().map(String::valueOf).collect(Collectors.joining(",")) + "|" +
//                card.getDesc().replace("\n", "\\n");
//    }



    public void saveallCards() {
        for (Card card : Cards.values()) {
            databaseHelper.insertCardIntoSQLite(card);
        }
    }



    public void loadallcards(){
        ArrayList<Card> loadedCards = DatabaseHelper.loadCards();

        for (Card card : loadedCards) {
            addCard(card);
            CardArray.add(card.getId());
    }
    }

    public void addCard(Card card) {
        Cards.put(card.getId(), card);

    }

    public Integer addCardbyinfo(String name, String Desc, String path, int level, String affinity, int baseHp, int basedef, int baseatk, int basecrit, String rarity) {
        Random rand = new Random();
        int id = generateUniqueId(rand);

        Card card = new Card(id, name, id, Desc, path, new Stats(level, affinity, baseHp, basedef, baseatk, basecrit, rarity));
        addCard(card);
        databaseHelper.insertCardIntoSQLite(card);; // Assuming this method inserts the card into the database

        return id;
    }


    public int generateUniqueId(Random rand) {
        int id;
        do {
            id = rand.nextInt(8999999) + 1000000;
        } while (databaseHelper.idExistsInDatabase(id));
        return id;
    }


    @Override
    public Card getCard(int cardId) {
        return Cards.get(cardId);
    }

//    public void updateCard(Card card) {
//        if (Cards.containsKey(card.getId())) {
//            Cards.put(card.getId(), card);
//
//        }
//    }

    public void setDescription(int cardId, String newDescription) {
        if (Cards.containsKey(cardId)) {
            Card card = Cards.get(cardId);
            card.setDesc(newDescription);
            Cards.put(cardId, card);

        }
    }

    public void removeCard(int cardId) {
        Cards.remove(cardId);
    }

    public void imagesave(String imgpath, Card card) throws IOException {
        // Uses to image link to retrieve the image download it and store it in images/
        // Uses card.id to name the image like 3823.jpg and then updates the card's path attribute
        Path imageDirectory = Paths.get("src/DB/Images");
        if (!Files.exists(imageDirectory)) {
            Files.createDirectories(imageDirectory);
        }

        // Create the file name using the card ID
        String fileName = card.getId() + ".jpg";
        Path imagePath = imageDirectory.resolve(fileName);

        // Download the image
        URL url = new URL(imgpath);
        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");

        try (InputStream in = httpcon.getInputStream()) {
            Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Update the card's image path
        card.setImgpath(imagePath.toString());

        saveallCards();


    }

    @Override
    public void setActiveUser(User user){
        this.Commonuser = user;
    }

    @Override
    public ArrayList<Integer> CardManager(){
        return CardArray;
    }

    public void exit(){
        saveallCards();
    }

    public void addUser(User user) {
        DatabaseHelper.saveUser(user);
    }

    // Retrieve a user by their ID
    public User getUser(String Username) {
        return databaseHelper.loadUser(Username);
    }


    @Override
    public Integer randomcard(){
        Random rand = new Random();
        return CardArray.get(rand.nextInt(CardArray.size()));
    }


    @Override
    public void updateusercard(Integer cardID){
        Commonuser.addownedcard(cardID);
        databaseHelper.saveUser(Commonuser);
    }

    @Override
    public User getActiveUser(){
        return Commonuser;
    }
}

