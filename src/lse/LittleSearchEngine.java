package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		HashMap<String,Occurrence> docKeys = new HashMap<String,Occurrence>(1000, 2.0f);
		
		Scanner scanner = new Scanner(new File (docFile));
		while (scanner.hasNext()){
			String k = getKeyword(scanner.next());
			if (k == null){
				continue;
			}
			Occurrence previous = docKeys.get(k);
			if (previous != null){
				previous.frequency++;
			} else {
				previous = new Occurrence(docFile, 1);
				docKeys.put(k, previous);
			}
		}
		scanner.close();
		/** COMPLETE THIS METHOD **/
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return docKeys;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for (String k : kws.keySet()){
			
			Occurrence occurrence = kws.get(k);

			if (keywordsIndex.containsKey(k)){
				ArrayList<Occurrence> occur = keywordsIndex.get(k);;
				occur.add(occurrence);
				insertLastOccurrence(occur);
			} 
			else {
				ArrayList <Occurrence> occur = new ArrayList<>();
				occur.add(occurrence);
				insertLastOccurrence(occur);
				keywordsIndex.put(k, occur);
			}
		}
		/** COMPLETE THIS METHOD **/
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		word = word.replaceAll("[\\.|\\,|\\?|\\:|\\;|\\!]+$", "");
		for (int i = 0; i < word.toCharArray().length; i++){
			if (!Character.isAlphabetic(word.toCharArray()[i])){
				return null;
			}
		}
		if (noiseWords.contains(word.toLowerCase())){
			return null;
		} else {
			return word.toLowerCase();
		}
		/** COMPLETE THIS METHOD **/
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		ArrayList<Integer> middle = new ArrayList<Integer>();
		int highKey = occs.size() - 2;
		int lowKey = 0;
		int middleKey = (highKey + lowKey) / 2;
		int finalIndex = highKey + 1;

		Occurrence lOccurence = occs.get(finalIndex);
		while (lowKey <= highKey) {
			middleKey =(highKey + lowKey) / 2;
			middle.add(middleKey);
			if(lOccurence.frequency < occs.get(middleKey).frequency){
				lowKey = middleKey + 1;
			} else if (lOccurence.frequency > occs.get(middleKey).frequency){
				highKey = middleKey - 1;
			} else {
				break;
			}
 		}
		if (lOccurence.frequency >= occs.get(middleKey).frequency){
			occs.add(middleKey, occs.remove(finalIndex));
		}
		/** COMPLETE THIS METHOD **/
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return middle;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> doc = new ArrayList<String>();
		ArrayList<Occurrence> keySearch1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> keySearch2 = keywordsIndex.get(kw2);
		int indexForOne = 0;
		int indexForTwo = 0;

		while (doc.size() != 5){
			Occurrence keyOne = null;
			Occurrence keyTwo = null;
			try {
				keyOne = keySearch1.get(indexForOne);
			} 
			catch (IndexOutOfBoundsException e){}
			catch (NullPointerException e){}
			try {
				keyTwo = keySearch2.get(indexForTwo);
			}
			catch (IndexOutOfBoundsException e){}
			catch (NullPointerException e){}
			if (keyOne == null && keyTwo == null){
				break;
			}
			if (keyTwo == null){
				if (!doc.contains(keyOne.document)){
					doc.add(keyOne.document);
				}
				indexForOne++;
				continue;
			}
			if (keyOne == null){
				if (!doc.contains(keyTwo.document)){
					doc.add(keyTwo.document);
				}
				indexForTwo++;
				continue;
			}
			int keyOneFreq = keyOne.frequency;
			int keyTwoFreq = keyTwo.frequency;

			if (keyOneFreq < keyTwoFreq){
				if (!doc.contains(keyTwo.document)){
					doc.add(keyTwo.document);
				}
				indexForTwo++;
			} else {
				if (!doc.contains(keyOne.document)){
					doc.add(keyOne.document);
				}
				indexForOne++;
			}
		}
		if (doc.size() == 0){
			return null;
		} else {
			return doc;
		}
		/** COMPLETE THIS METHOD **/
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
	}
}
