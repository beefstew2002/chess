package dataaccess;

public interface DAInterface {
    //In order to abstract from your services where data is actually being stored,
    //you must create a Java interface that hides all of the implementation details
    //for accessing and retrieving data. In this phase you will create an implementation
    //of your data access interface that stores your server's data in main memory (RAM)
    //using standard data structures (maps, sets, lists). In the next phase you
    //will create an implementation of the data access interface that uses an external
    //SQL database.

    //So for Phase 3 purposes, it's just going to store everything internally
    //In an interface? How is everything going to access it?
}
