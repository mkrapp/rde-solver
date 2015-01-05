package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class Writer {

    public static void write(String fileName, String text) {
	try {
	    PrintWriter pw = new PrintWriter(new File(fileName)
		    .getAbsoluteFile());
	    try {
		pw.println(text);
	    } finally {
		pw.close();
	    }
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }
    
    public static void add(String fileName, String text) {
	try {

	    PrintWriter pw = new PrintWriter(new FileOutputStream(fileName, true));
	    try {
//		pw.println(text);
		pw.write(text);
	    } finally {
		pw.close();
	    }
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Stores an object to a file. It makes use of
     * <code>{@link FileOutputStream}</code> and
     * <code>{@link ObjectOutputStream}</code>.
     * 
     * @param fileName
     *                name of the file where the object will be stored.
     */
    public static void storeObject(String fileName, Object object) {
	FileOutputStream fos;
	ObjectOutputStream oos;
	try {
	    fos = new FileOutputStream(fileName);
	    oos = new ObjectOutputStream(fos);
	    oos.writeObject(object);
	    oos.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Loads an object from a file. It makes use of
     * <code>{@link FileInputStream}</code> and
     * <code>{@link ObjectInputStream}</code>.
     * 
     * @param fileName
     *                name of the file where the object will be loaded from.
     * @return a new object.
     */
    public static Object loadObject(String fileName) {
	FileInputStream fis;
	ObjectInputStream ois;
	Object object = null;
	try {
	    fis = new FileInputStream(fileName);
	    ois = new ObjectInputStream(fis);
	    object = ois.readObject();
	    ois.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	return object;
    }
}
