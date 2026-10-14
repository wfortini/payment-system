db = db.getSiblingDB("executordb");

if (!db.getCollectionNames().includes("payment-executors")) {
    db.createCollection("payment-executors");
}
