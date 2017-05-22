import java.util.*;
import java.io.*;
/**
 * 
 * @author amarl_000
 * Main class 
 */
public class Main {
	/**
	 * main method, runs the program
	 * @param args
	 */
	public static void main(String[] args){
		int action = 1;
		Scanner kbrd = new Scanner(System.in);
		while(action>0&&action<5){
			System.out.println("Options:\n 1.Add Student \n 2.Find Student \n 3.Delete Student \n 4.Show All");
			action = kbrd.nextInt();
			kbrd.nextLine();
			if(action==1){
				System.out.println("Enter the name to insert:");
				String name = kbrd.nextLine();
				boolean success = addStudent(name);
				if(success){
					System.out.println(name+" added successfully. Their hash value is: "+getHash(name));
				}
			}
			else if(action==2){
				System.out.println("Enter the name or hash to find:");
				String value = kbrd.nextLine();
				findVal(value);
				
			}
			else if(action==3){
				System.out.println("Enter the name to delete:");
				String value = kbrd.nextLine();
				System.out.println(deleteStudent(value));
			}
			else if(action==4){
				showAll();
			}
		}
	}
	/**
	 * opens the hash table file for access as an ArrayList
	 * @return an array list holding the hash table
	 */
	public static ArrayList<String> setUpFile(){
		ArrayList<String> info = new ArrayList<>(256);
		for(int i = 0; i<256;i++){
			info.add("-1");
		}
		File file = new File("hashTable.txt");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Scanner read = null;
		try {
			read = new Scanner(file);
			int line = 0;
			while(read.hasNextLine()){
				String data = read.nextLine();
				if(data.length()>0){
					info.set(line, data);}
				else{
					info.set(line, "-1");
				}
				line++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		read.close();
		file.delete();
		return info;
	}
	/**
	 * Overwrites the hash table file after editing as an ArrayList
	 * @param info the hash table as an ArrayList
	 */
	public static void writeFile(ArrayList<String> info){
		File file = new File("hashTable.txt");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			for(int i = 0;i<info.size();i++){
				bw.write(info.get(i));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * adds a student to the hash table
	 * @param name the name of the student to be added
	 * @return true if the add is successful
	 */
	public static boolean addStudent(String name){
		ArrayList<String> info = setUpFile();
		int hash = getHash(name);
		int pos = hash%256;
		if(!info.get(pos).equals("-1")){
			boolean cont = true;
			while(cont&&pos<256){
				if(info.get(pos).equals("-1")){
					info.set(pos, name+" "+hash);
					writeFile(info);
					return true;
				}
				else{pos++;}
			}
			if(cont){
				pos=0;
				while(cont&&pos<(hash%256)){
					if(info.get(pos).equals("-1")){
						info.set(pos, name+" "+hash);
						writeFile(info);
						return true;
					}
					else{pos++;}
				}
			}
			if(cont){
				System.out.println("Hash Table is full. Failed to add"+name);
				writeFile(info);
				return false;
			}
		}
		else{
			info.set(pos, name+" "+hash);
			writeFile(info);
			return true;
		}
		writeFile(info);
		return false;
	}
	/**
	 * generates the hash value of a string by XORing it with a key
	 * @param val the string to find a hash value for
	 * @return the hash value
	 */
	public static int getHash(String val){
		int key = 12;
		for(int i = 0;i<val.length();i++){
			key = key^val.charAt(i);
		}
		return key;
	}
	/**
	 * deletes a student from the hash table with the same name as the parameter
	 * @param name - the name of the student to be deleted
	 * @return the deleted student's name an hash value
	 */
	public static String deleteStudent(String name){
		String val = "";
		ArrayList<String> info = setUpFile();
		int hash = getHash(name);
		int pos = hash%256;
		boolean cont = true;
		if(info.get(pos).contains(name+" "+hash)){
			val = info.get(pos);
			info.set(pos, "-1");
		}
		else{
			while(pos<256&&cont){
				if(info.get(pos).contains(name+" "+hash)){
					val = info.get(pos);
					info.set(pos, "-1");
					cont = false;
				}
				pos++;
			}
			pos=0;
			while(pos<(hash%256)&&cont){
				if(info.get(pos).contains(name+" "+hash)){
					val = info.get(pos);
					info.set(pos, "");
					cont = false;
				}
				pos++;
			}
		}
		writeFile(info);
		if(val.length()>0){
			return val;}
		else{
			return "Could not find "+name;
		}
	}
	/**
	 * finds and prints a student's name and hash value based on the name or hash value provided
	 * @param val the name or hash value to search for
	 */
	public static void findVal(String val){
		int hash = 0;
		String name = "";
		try{
			hash = Integer.parseInt(val);
		}
		catch(NumberFormatException e){
			hash = getHash(val);
			name = val;
		}
		ArrayList<String> info = setUpFile();
		int pos = hash%256;
		boolean cont = true;
		if(info.get(pos).contains(name+" "+hash)){
			System.out.println(info.get(pos));
			cont = false;
		}
		else{
			while(pos<256&&cont){
				if(info.get(pos).contains(name+" "+hash)){
					System.out.println(info.get(pos));
					cont = false;
				}
				pos++;
			}
			pos=0;
			while(pos<(hash%256)&&cont){
				if(info.get(pos).contains(name+" "+hash)){
					System.out.println(info.get(pos));
					cont = false;
				}
				pos++;
			}
		}
		if(cont){
			System.out.println("Could not find "+val);
		}
		writeFile(info);
		
	}
	/**
	 * prints all students and hash values in the hash table
	 */
	public static void showAll(){
		ArrayList<String> info = setUpFile();
		for(int i = 0;i<info.size();i++){
			if(info.get(i).contains(" ")){
				System.out.println(info.get(i));
			}
		}
		writeFile(info);
	}
	
}
