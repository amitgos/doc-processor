package com.chaturs.models;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.geometerplus.android.fbreader.AnnotateActivity;
import org.geometerplus.zlibrary.ui.android.R;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;
import android.widget.AnalogClock;

import com.chaturs.HomeActivity;
import com.chaturs.NotePaint;

public class DatabaseHandler {

	public static final int IO_BUFFER_SIZE = 4*1024;
	private static DatabaseHandler instance = null;
	private SQLiteDatabase sqliteDatabase = null;
	private static final String BOOKS_PATH = Environment
			.getExternalStorageDirectory()
			+ "/Books/DocProcessor/";

	public static DatabaseHandler getInstance() {
		if (instance == null) {
			instance = new DatabaseHandler();
		}
		return instance;
	}

	private DatabaseHandler() {
		try {
			sqliteDatabase = HomeActivity.getInstance().openOrCreateDatabase(
					"docprocessorbooks.db", Context.MODE_PRIVATE, null);
			createBookTable();
		} catch (Exception e) {
			Log.e("Dtabase Handler",""+ e.getMessage());
		}
		
	}

	private void copy(InputStream in, FileOutputStream out) throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	private void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

	private void copyDataToSDCard() {
		
	//Adventure
//		copyRawFileToSdcard(R.raw.around_the_world_in_eighty_days_epub, "around_the_world_in_eighty_days.epub");
		copyRawFileToSdcard(R.raw.around_the_world_in_eighty_days, "around_the_world_in_eighty_days.png");
//		copyRawFileToSdcard(R.raw.moby_dick_epub, "moby_dick.epub");
		copyRawFileToSdcard(R.raw.moby_dick, "moby_dick.png");
//		copyRawFileToSdcard(R.raw.the_adventures_of_tom_sawyer_epub,"the_adventures_of_tom_sawyer_epub.epub");
		copyRawFileToSdcard(R.raw.the_adventures_of_tom_sawyer, "the_adventures_of_tom_sawyer.png");
//		copyRawFileToSdcard(R.raw.the_call_of_the_wild_epub,"the_call_of_the_wild_epub.epub");
		copyRawFileToSdcard(R.raw.the_call_of_the_wild, "the_call_of_the_wild.png");
//		copyRawFileToSdcard(R.raw.the_three_musketeers_epub, "the_three_musketeers_epub.epub");
		copyRawFileToSdcard(R.raw.the_three_muskteers, "the_three_musketeers.png");
//		copyRawFileToSdcard(R.raw.twentyooo_leagues_under_the_sea_epub, "twentyooo_leagues_under_the_sea_epub.EPUB");
		copyRawFileToSdcard(R.raw.twentyooo_leagues_under_the_sea, "twentyooo_leagues_under_the_sea.png");
		
	//Crime-Mystery
//		copyRawFileToSdcard(R.raw.the_hound_of_the_baskervilles_epub, "the_hound_of_the_baskervilles_epub.epub");
		copyRawFileToSdcard(R.raw.the_hound_of_the_baskervilles, "the_hound_of_the_baskervilles.jpg");
//		copyRawFileToSdcard(R.raw.the_man_who_was_thursday_a_nightmare_epub, "the_man_who_was_thursday_a_nightmare_epub.epub");
		copyRawFileToSdcard(R.raw.the_man_who_was_thursday_a_nightmare, "the_man_who_was_thursday_a_nightmare.jpg");
//		copyRawFileToSdcard(R.raw.the_valley_of_fear_epub, "the_valley_of_fear_epub.epub");
		copyRawFileToSdcard(R.raw.the_valley_of_fear, "the_valley_of_fear.jpg");
//		copyRawFileToSdcard(R.raw.the_most_dangerous_game_epub, "the_most_dangerous_game_epub.epub");
		copyRawFileToSdcard(R.raw.the_most_dangerous_game, "the_most_dangerous_game.jpg");
//		copyRawFileToSdcard(R.raw.the_sign_of_the_four_epub, "the_sign_of_the_four_epub.epub");
		copyRawFileToSdcard(R.raw.the_sign_of_the_four, "the_sign_of_the_four.jpg");
		
	//Travel
//		copyRawFileToSdcard(R.raw.twilight_in_italy_epub, "twilight_in_italy_epub.epub");
		copyRawFileToSdcard(R.raw.twilight_in_italy, "twilight_in_italy.jpg");
//		copyRawFileToSdcard(R.raw.the_road_epub,"the_road_epub.epub");
		copyRawFileToSdcard(R.raw.the_road,"the_road.jpg");
//		copyRawFileToSdcard(R.raw.the_travels_of_sir_john_mandeville_epub, "the_travels_of_sir_john_mandeville_epub.epub");
		copyRawFileToSdcard(R.raw.the_travels_of_sir_john_mandeville, "the_travels_of_sir_john_mandeville.jpg");
//		copyRawFileToSdcard(R.raw.the_itinerary_of_archbishop_baldwin_through_wales_epub, "the_itinerary_of_archbishop_baldwin_through_wales_epub.epub");
		copyRawFileToSdcard(R.raw.the_itinerary_of_archbishop_baldwin_through_wales, "the_itinerary_of_archbishop_baldwin_through_wales.jpg");
//		copyRawFileToSdcard(R.raw.roughing_it_epub, "roughing_it_epub.epub");
		copyRawFileToSdcard(R.raw.roughing_it, "roughing_it.jpg");
//		copyRawFileToSdcard(R.raw.five_weeks_in_a_balloon_epub, "five_weeks_in_a_balloon_epub.epub");
		copyRawFileToSdcard(R.raw.five_weeks_in_a_balloon, "five_weeks_in_a_balloon.jpg");
		
	//Religion
//		copyRawFileToSdcard(R.raw.a_tale_of_christ_epub, "a_tale_of_christ_epub");
		copyRawFileToSdcard(R.raw.a_tale_of_christ, "a_tale_of_christ.png");
//		copyRawFileToSdcard(R.raw.siddhartha_epub, "siddhartha_epub.epub");
		copyRawFileToSdcard(R.raw.siddhartha, "siddhartha.png");
//		copyRawFileToSdcard(R.raw.tao_te_ching_epub, "tao_te_ching_epub.epub");
		copyRawFileToSdcard(R.raw.tao_te_ching, "tao_te_ching.png");
//		copyRawFileToSdcard(R.raw.where_love_is_there_god_is_also_epub, "where_love_is_there_god_is_also_epub.epub");
		copyRawFileToSdcard(R.raw.where_love_is_there_god_is_also, "where_love_is_there_god_is_also.png");
//		copyRawFileToSdcard(R.raw.the_age_of_reason_epub, "the_age_of_reason_epub.epub");
		copyRawFileToSdcard(R.raw.the_age_of_reason,"the_age_of_reason.png");
//		copyRawFileToSdcard(R.raw.the_pilgrims_progress_epub, "the_pilgrims_progress_epub.epub");
		copyRawFileToSdcard(R.raw.the_pilgrims_progress, "the_pilgrims_progress.png");
		
		List<Category> categories = listCategories();			
	}

	private void copyRawFileToSdcard(int resourceId, String fileName) {

		File file = new File(BOOKS_PATH);
		file.mkdirs();

		InputStream inputStream = HomeActivity.getInstance().getResources()
				.openRawResource(resourceId);

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(
					BOOKS_PATH + fileName));
			try {
				copy(inputStream, fileOutputStream);
			} catch (IOException e) {
			} finally {
				closeStream(inputStream);
				closeStream(fileOutputStream);
			}

		} catch (FileNotFoundException e) {
		}
	}

	private void createBookTable() {

		try {
			sqliteDatabase.execSQL("CREATE TABLE Books("
					+ "book_id INTEGER PRIMARY KEY," + "title TEXT,"
					+ "author TEXT," + "category TEXT," + "file_path TEXT,"
					+ "thumb_path TEXT)");

			sqliteDatabase.execSQL("CREATE TABLE Categories(" +
					"category_id INTEGER PRIMARY KEY, name TEXT)");
			
			sqliteDatabase.execSQL("CREATE TABLE StudyGroups(" +
					"studygroup_id INTEGER PRIMARY KEY,studygroup_server_id INTEGER , name TEXT)");
			
			sqliteDatabase.execSQL("CREATE TABLE Problems(" +
					"problem_id INTEGER PRIMARY KEY, problem_server_id INTEGER ,title TEXT," +
					"studygroup_id INTEGER, problem_user TEXT , problem_time DATETIME)");
			
			sqliteDatabase.execSQL("CREATE TABLE Annotations(" +
					"annotation_id INTEGER PRIMARY KEY, annotation_server_id INTEGER , problem_server_id INTEGER," +
					"annotate_local_path TEXT,annotate_server_path TEXT, annotated_text TEXT)");
			
			sqliteDatabase.execSQL("CREATE TABLE NotePaints(" +
					"notepaint_id INTEGER PRIMARY KEY, " +
					"notepaint_path TEXT, notepaint_title TEXT)");
			
			sqliteDatabase.execSQL("CREATE TABLE Notes(" +
					"note_id INTEGER PRIMARY KEY, " +
					"note_server_id INTEGER,"+
					"studygroup_server_id INTERGER,"+
					"note_text TEXT, note_title TEXT , note_user TEXT , note_time DATETIME)");
			sqliteDatabase.execSQL("CREATE TABLE Solutions(" +
					"solution_id INTEGER PRIMARY KEY, solution_server_id INTEGER , problem_server_id INTEGER," +
					"solution_text TEXT , solution_user TEXT )");
			
			
			copyDataToSDCard();
			populateHardcodedData();
			
		} catch (SQLException sqlException) {

		}
	}

	private void populateHardcodedData() {
		
		insertCategory("Adventure");
		insertCategory("Crime-Mystery");
		insertCategory("Travel");
		insertCategory("Religion");

		Book book = new Book("", "Around the World in Eighty Days ", "Jules Verne", "Adventure",
				BOOKS_PATH + "around_the_world_in_eighty_days_epub.epub",
				BOOKS_PATH + "around_the_world_in_eighty_days.png");
		insertBook(book);
		
		book = new Book("", "The Whale", "Herman Merville", "Adventure",
				BOOKS_PATH + "moby_dick_epub.epub",
				BOOKS_PATH + "moby_dick.png");
		insertBook(book);
		
		
		book = new Book("", "The Adventures of Tom Sawyer", "Mark Twain", "Adventure",
				BOOKS_PATH + "the_adventures_of_tom_sawyer_epub.epub",
				BOOKS_PATH + "the_adventures_of_tom_sawyer.png");
		insertBook(book);
		
		book = new Book("", "The call Of The Wild", "Mark Twain", "Adventure",
				BOOKS_PATH + "the_call_of_the_wild_epub.epub",
				BOOKS_PATH + "the_call_of_the_wild.png");
		insertBook(book);
		
		book = new Book("", "The Three Musketeers", "Mark Twain", "Adventure",
				BOOKS_PATH + "the_three_musketeers_epub.epub",
				BOOKS_PATH + "the_three_musketeers.png");
		insertBook(book);
		
		
		book = new Book("", "TwentyToo Leagues Under The Sea", "Mark Twain", "Adventure",
				BOOKS_PATH + "twentyooo_leagues_under_the_sea_epub.epub",
				BOOKS_PATH + "twentyooo_leagues_under_the_sea.png");
		insertBook(book);
		
		//
		
		book = new Book("", "The Hound Of Baskervilles", "Arthoe canon Doyle", "Crime-Mystery",
				BOOKS_PATH + "the_hound_of_the_baskervilles_epub.epub",
				BOOKS_PATH + "the_hound_of_the_baskervilles.jpg");
		insertBook(book);
		
		book = new Book("", "The Man who was Thursday : A Nightmare ", "Gilbert Keith Chesteron", "Crime-Mystery",
				BOOKS_PATH + "the_man_who_was_thursday_a_nightmare_epub.epub",
				BOOKS_PATH + "the_man_who_was_thursday_a_nightmare.jpg");
		insertBook(book);
		
		book = new Book("", "The Valley Of Fear ", "Arthor Canon Doyle", "Crime-Mystery",
				BOOKS_PATH + "the_valley_of_fear_epub.epub",
				BOOKS_PATH + "the_valley_of_fear.jpg");
		insertBook(book);
		
		book = new Book("", "The Most Dangerous Game ", "Richard Connell", "Crime-Mystery",
				BOOKS_PATH + "the_most_dangerous_game_epub.epub",
				BOOKS_PATH + "the_most_dangerous_game.jpg");
		insertBook(book);
		
		book = new Book("", "The Sign Of Four ", "Arthor canon Doyle", "Crime-Mystery",
				BOOKS_PATH + "the_sign_of_the_four_epub.epub",
				BOOKS_PATH + "the_sign_of_the_four.jpg");
		insertBook(book);
		
		
		//
		
		book = new Book("", "Twilight In Italy ", "David herbert Lawerence", "Travel",
				BOOKS_PATH + "twilight_in_italy_epub.epub",
				BOOKS_PATH + "twilight_in_italy.jpg");
		insertBook(book);
		
		book = new Book("", "The Road", "Jack London", "Travel",
				BOOKS_PATH + "the_road_epub.epub",
				BOOKS_PATH + "the_road.jpg");
		insertBook(book);
		
		book = new Book("", "The travels Of Sir John mandeVille", "sir john mandeville", "Travel",
				BOOKS_PATH + "the_travels_of_sir_john_mandeville_epub.epub",
				BOOKS_PATH + "the_travels_of_sir_john_mandeville.jpg");
		insertBook(book);
		
		book = new Book("", "The itinerary Of Archibishop Baldwin Through wales", "Giraldus Cambrensis", "Travel",
				BOOKS_PATH + "the_itinerary_of_archbishop_baldwin_through_wales_epub.epub",
				BOOKS_PATH + "the_itinerary_of_archbishop_baldwin_through_wales.jpg");
		insertBook(book);
		
		book = new Book("", "Five Weeks In a Balloon","Jules verne", "Travel",
				BOOKS_PATH + "five_weeks_in_a_balloon_epub.epub", BOOKS_PATH + "five_weeks_in_a_balloon.jpg");
		insertBook(book);
		
		book = new Book("", "Roughing It","Mark twain", "Travel",
				BOOKS_PATH + "roughing_it_epub.epub", BOOKS_PATH + "roughing_it.jpg");
		insertBook(book);
		
		//
		book = new Book("", "A Tale Of Christ","Lewis Wallace", "Religion",
				BOOKS_PATH + "a_tale_of_christ_epub.epub", BOOKS_PATH + "a_tale_of_christ.png");
		insertBook(book);
		book = new Book("", "Siddhartha","Hermann Hesse", "Religion",
				BOOKS_PATH + "siddhartha_epub.epub", BOOKS_PATH + "siddhartha.png");
		insertBook(book);
		book = new Book("", "Tao Te Ching","Laozi", "Religion",
				BOOKS_PATH + "tao_te_ching_epub.epub", BOOKS_PATH + "tao_te_ching.png");
		insertBook(book);
		book = new Book("", "Where Love Is , There God Is Also","Lev Nikolayevich Tolstoy", "Religion",
				BOOKS_PATH + "where_love_is_there_god_is_also_epub.epub", BOOKS_PATH + "where_love_is_there_god_is_also.png");
		insertBook(book);
		
		book = new Book("", "The Age Of Reason","Thomas Paine", "Religion",
				BOOKS_PATH + "the_age_of_reason_epub.epub", BOOKS_PATH + "the_age_of_reason.png");
		insertBook(book);
		
		book = new Book("", "The Pilgrim's Progress","John Bunyan", "Religion",
				BOOKS_PATH + "the_pilgrims_progress_epub.epub", BOOKS_PATH + "the_pilgrims_progress.png");
		insertBook(book);
		
	}

	
	public SQLiteDatabase getSqliteDatabase() {
		return sqliteDatabase;
	}

	private void insertBook(Book book) {
		SQLiteStatement sqliteStatement = sqliteDatabase
				.compileStatement("insert into Books (title, author, category,"
						+ " file_path, thumb_path) Values (?,?,?,?,?)");
		sqliteStatement.bindString(1, book.getTitle());
		sqliteStatement.bindString(2, book.getAuthor());
		sqliteStatement.bindString(3, book.getCategory());
		sqliteStatement.bindString(4, book.getFilePath());
		sqliteStatement.bindString(5, book.getThumbNailPath());
		sqliteStatement.execute();
		sqliteStatement.close();
	}

	public void insertPaint(NotePaint notepaint){
		
		SQLiteStatement sqliteStatement = sqliteDatabase.compileStatement("insert into NotePaints" +
				" (notepaint_title, notepaint_path) Values(?,?)");
		sqliteStatement.bindString(1, notepaint.getTitle());
		sqliteStatement.bindString(2, notepaint.getPath());
		sqliteStatement.execute();
		sqliteStatement.close();
		
	}
	
	public List<NotePaint> listNotePaints(){
	
	List<NotePaint> notePaintList = new ArrayList<NotePaint>();
		
		Cursor cursor = sqliteDatabase.rawQuery("select notepaint_id, notepaint_title, notepaint_path from NotePaints" ,null );
		
		while(null !=cursor && cursor.moveToNext()) {
			notePaintList.add(new NotePaint(cursor.getLong(0), cursor.getString(1), cursor.getString(2)));
		}
		cursor.close();
		return notePaintList;
	
	}
	
	public void updatePaint(NotePaint notepaint){
		
		Cursor countCursor = sqliteDatabase.rawQuery("select count(*) from NotePaints where notepaint_id=?" , new String[]{ String.valueOf(notepaint.getId())} );
		
		if((null !=countCursor && countCursor.moveToNext())) {
			int count = countCursor.getInt(0);
			
			if(count > 0) {
				SQLiteStatement sqliteStatement = sqliteDatabase.compileStatement("update NotePaints" +
				" set notepaint_title=?, notepaint_path=? where notepaint_id=?");
				sqliteStatement.bindString(1, notepaint.getTitle());
				sqliteStatement.bindString(2, notepaint.getPath());
				sqliteStatement.bindLong(3, notepaint.getId());
				sqliteStatement.execute();
				sqliteStatement.close();
			} else {
				insertPaint(notepaint);
			}
		}
		countCursor.close();
	}
	
	public void deletePaint(NotePaint notepaint) {
		SQLiteStatement sqliteStatement = sqliteDatabase.compileStatement("delete from NotePaints" +
		" where notepaint_id=?");
		sqliteStatement.bindLong(1, notepaint.getId());
		sqliteStatement.execute();
		sqliteStatement.close();
	}
	
	
	private String getProblemName(List<Annotate> annotationList){
		
		String string = null;
		for(int i=0;i<annotationList.size();i++){
			string = annotationList.get(i).getText();
			if(string != null && string.length() > 0 ){
				break;
			}
		}
		return string;
	}
	
	public void insertProblem(Problem problem) {
		
		Cursor countCursor = sqliteDatabase.rawQuery("select count(*) from Problems where problem_server_id=? and studygroup_id=?" ,
				new String[]{ String.valueOf(problem.getServerId()), String.valueOf(problem.getStudygroupServerId())} );
		List<Annotate> annotationList = problem.getAnnotateList();
		List<Solution> solutionList = problem.getSolutionList();
		if((null !=countCursor && countCursor.moveToNext())) {
			int count = countCursor.getInt(0);
			
			if(count < 1) {
							
				SQLiteStatement sqliteStatement = sqliteDatabase.compileStatement("insert into Problems" +
						" ( problem_server_id, title, studygroup_id,problem_user,problem_time) Values(?,?,?,?,?)");
				
				sqliteStatement.bindLong(1, problem.getServerId());
				sqliteStatement.bindString(2, problem.getName());
				sqliteStatement.bindLong(3, problem.getStudygroupServerId());
				sqliteStatement.bindString(4, problem.getUserName());
				sqliteStatement.bindString(5, problem.getTime());
				sqliteStatement.execute();
				sqliteStatement.close();
				
					for(int i=0;i<annotationList.size();i++){
						insertAnnotate(problem.getServerId(), annotationList.get(i));
					}
			} 
		}
		for(int i=0;i<solutionList.size();i++){
			insertSolution(problem.getServerId(),solutionList.get(i));
		}
		countCursor.close();
	}
	
	public void insertAnnotate(long problemServerId ,Annotate annotate ){
		
		Cursor countCursor = sqliteDatabase.rawQuery("select count(*) from Annotations where annotation_server_id=? and problem_server_id=?" ,
				new String[]{ String.valueOf(annotate.getServerId()),String.valueOf(problemServerId)} );
		
		if((null !=countCursor && countCursor.moveToNext())) {
			int count = countCursor.getInt(0);
			
			if(count < 1) {
				SQLiteStatement sqliteStatement = sqliteDatabase.compileStatement("insert into Annotations" +
				" (annotation_server_id,problem_server_id,annotate_local_path,annotated_text,annotate_server_path) Values(?,?,?,?,?)");
				sqliteStatement.bindLong(1,annotate.getServerId());
				sqliteStatement.bindLong(2, problemServerId);
				sqliteStatement.bindString(3, annotate.getLocalImagePath());
				sqliteStatement.bindString(4, annotate.getText());
				sqliteStatement.bindString(5, annotate.getServerImagePath());
				sqliteStatement.execute();
				sqliteStatement.close();

			} 
		}
		countCursor.close();
	}
	
	public void insertSolution(long problemServerId ,Solution solution ){
		
		Cursor countCursor = sqliteDatabase.rawQuery("select count(*) from Solutions where solution_server_id=? and problem_server_id=?" ,
				new String[]{ String.valueOf(solution.getServerId()),String.valueOf(problemServerId)} );
		
		if((null !=countCursor && countCursor.moveToNext())) {
			int count = countCursor.getInt(0);
			
			if(count < 1) {
				SQLiteStatement sqliteStatement = sqliteDatabase.compileStatement("insert into Solutions" +
				" (solution_server_id,problem_server_id,solution_text,solution_user) Values(?,?,?,?)");
				sqliteStatement.bindLong(1,solution.getServerId());
				sqliteStatement.bindLong(2, problemServerId);
				sqliteStatement.bindString(3, solution.getSolution());
				sqliteStatement.bindString(4, solution.getUser());
				sqliteStatement.execute();
				sqliteStatement.close();

			} 
		}
		countCursor.close();
	}
	
	public void insertNote(Note note){
		
		Cursor countCursor = sqliteDatabase.rawQuery("select count(*) from notes where studygroup_server_id = ? and note_server_id=? " , 
				new String[]{String.valueOf(note.getStudygroupId()), String.valueOf(note.getServerId())} );
		
		if((null !=countCursor && countCursor.moveToNext())) {
			int count = countCursor.getInt(0);
			
			if(count < 1) {
				SQLiteStatement sqLiteStatement = sqliteDatabase.compileStatement("insert into notes" +
				"(note_server_id , note_title , note_text,studygroup_server_id ,note_user,note_time) Values(?,?,?,?,?,?)");
			
			sqLiteStatement.bindLong(1, note.getServerId());
			sqLiteStatement.bindString(2, note.getTitle());
			sqLiteStatement.bindString(3, note.getContent());
			sqLiteStatement.bindLong(4, note.getStudygroupId());
			sqLiteStatement.bindString(5, note.getUserName());
			sqLiteStatement.bindString(6, note.getTime());
			sqLiteStatement.execute();
			sqLiteStatement.close();
			} 
		}
		countCursor.close();
	}
	
	public List<Category> listCategories() {
		List<Category> list = new ArrayList<Category>();
		
		Cursor cursor = sqliteDatabase.rawQuery("SELECT name FROM Categories", null);
		while (cursor.moveToNext()) {
			list.add(new Category(cursor.getString(0), bookListForCategory(cursor.getString(0))));
		}
		cursor.close();
		return list;
	}
	
	public List<StudyGroup> listStudyGroups() {
		List<StudyGroup> list = new ArrayList<StudyGroup>();
		
		Cursor cursor = sqliteDatabase.rawQuery("SELECT studygroup_server_id , name FROM StudyGroups", null);
		while(cursor.moveToNext()) {
			list.add(new StudyGroup(cursor.getLong(0),cursor.getString(1), problemListForStudyGroup(cursor.getLong(0)),notesListForStudyGroup(cursor.getLong(0))));
		}
		cursor.close();
		return list;
	}
	
	public List<Long> listStudyGroupServerIds(){
		
		List<Long> list = new ArrayList<Long>();
		
		Cursor cursor = sqliteDatabase.rawQuery("SELECT studygroup_server_id FROM StudyGroups", null);
		while(cursor.moveToNext()) {
			list.add(cursor.getLong(0));
		}
		cursor.close();
		return list;
	}
	public List<String> listStudyGroupNames(){
		
		List<String> list = new ArrayList<String>();
		
		Cursor cursor = sqliteDatabase.rawQuery("SELECT name FROM StudyGroups", null);
		while(cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}
		cursor.close();
		return list;
	}

	public List<StudyGroup> listStudyGroupsInfo() {
		List<StudyGroup> list = new ArrayList<StudyGroup>();
		
		Cursor cursor = sqliteDatabase.rawQuery("SELECT studygroup_server_id , name FROM StudyGroups", null);
		while(cursor.moveToNext()) {
			list.add(new StudyGroup(cursor.getLong(0),cursor.getString(1), null,null));
		}
		cursor.close();
		return list;
	}
	
	public List<Problem> problemListForStudyGroup(long studygroup_server_id) {
		List<Problem> list  = new ArrayList<Problem>();
		
		Cursor cursor = sqliteDatabase.rawQuery("SELECT problem_server_id ,problem_id, title, studygroup_id ,problem_user ," +
				" problem_time FROM Problems where studygroup_id=? order by problem_time desc", new String[] { "" + String.valueOf(studygroup_server_id )});
		while(cursor.moveToNext()) {
			list.add(new Problem(cursor.getLong(0),
					String.valueOf(cursor.getInt(1)),
					cursor.getString(2),
					cursor.getLong(3),
					cursor.getString(4),
					cursor.getString(5),
					listAnnotations(cursor.getLong(0)),
					listSolutions(cursor.getLong(0))));

		}
		cursor.close();
		return list;
	}
	
	public List<Note> notesListForStudyGroup(long studygroupserverId){
		
		List<Note> list  = new ArrayList<Note>();
		Cursor cursor = sqliteDatabase.rawQuery("SELECT note_id,note_server_id,note_title,note_text,note_user,"+
			" note_time FROM notes where studygroup_server_id = ? order by note_time desc",new String[]{ String.valueOf(studygroupserverId)});
		while(cursor != null && cursor.moveToNext()){
			list.add(new Note(cursor.getLong(0),
					cursor.getLong(1),
					studygroupserverId,
					cursor.getString(2),
					cursor.getString(3),
					cursor.getString(4),
					cursor.getString(5)));
		}
		cursor.close();
		return list;
	}
	
	public List<Problem> problemListForquery(String query) {
		List<Problem> list  = new ArrayList<Problem>();
		query = "%" + query.toLowerCase().trim() + "%";
		Cursor cursor = sqliteDatabase.rawQuery("SELECT problem_server_id,problem_id, title, studygroup_id,problem_user,problem_time" +
				" FROM Problems where Lower(title) like ? or Lower(problem_user) like ?", new String[] { query , query});
		while(cursor.moveToNext()) {
			list.add(new Problem(cursor.getLong(0),
					String.valueOf(cursor.getInt(1)),
					cursor.getString(2),
					cursor.getLong(3),
					cursor.getString(4),
					cursor.getString(5),
					listAnnotations(cursor.getLong(0)),listSolutions(0)));
		}
		cursor.close();
		return list;
	}
	 
	public List<Note> noteListForquery(String query) {
		List<Note> list  = new ArrayList<Note>();
		query = "%" + query.toLowerCase().trim() + "%";
		Cursor cursor = sqliteDatabase.rawQuery("SELECT note_id,note_server_id,studygroup_server_id,note_title,note_text,note_user,"+
			"note_time  FROM notes where Lower(note_title) like ? or Lower(note_user)like ?",new String[]{query , query });
		while(cursor != null && cursor.moveToNext()){
			list.add(new Note(cursor.getLong(0),
					cursor.getLong(1),
					cursor.getLong(2),
					cursor.getString(3),
					cursor.getString(4),
					cursor.getString(5),
					cursor.getString(6)
					
			));
		}
		cursor.close();
		return list;
	}
	
	private List<Book> bookListForCategory(String categoryName) {
		List<Book> list = new ArrayList<Book>();
		
		Cursor cursor = sqliteDatabase.rawQuery("SELECT book_id, title, author, file_path, thumb_path" +
				" FROM Books where category =? order by title ", new String[] { "" + categoryName });
		while(cursor.moveToNext()) {
			list.add(new Book(String.valueOf(cursor.getInt(0)),
					cursor.getString(1),
					cursor.getString(2),
					categoryName,
					cursor.getString(3),
					cursor.getString(4)));
		}
		cursor.close();
		return list;
	}
	
	public List<Book> bookListForQuery(String query) {
		
		query = "%" + query.toLowerCase().trim() + "%";
		
		List<Book> list = new ArrayList<Book>();
		
		Cursor cursor = sqliteDatabase.rawQuery("SELECT book_id, title, author, category, file_path, thumb_path" +
				" FROM Books where LOWER(title) like ? or LOWER(author) like ? order by title ", new String[] { query, query });
		while(cursor.moveToNext()) {
			list.add(new Book(String.valueOf(cursor.getInt(0)),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getString(4),
					cursor.getString(5)));
		}
		cursor.close();
		return list;
	}
	
	public List<Annotate> listAnnotations(long problemId) {
		
		List<Annotate> annotationList = new ArrayList<Annotate>();
		
		Cursor cursor = sqliteDatabase.rawQuery("select annotation_server_id , annotation_id, annotated_text, " +
				"annotate_local_path , annotate_server_path from Annotations where problem_server_id=?" , new String[]{ String.valueOf(problemId)} );
		
		while(null !=cursor && cursor.moveToNext()) {
			annotationList.add(new Annotate(cursor.getLong(0),cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getString(4)));
		}
		cursor.close();
		return annotationList;
	}
	
	
	public List<Solution> listSolutions(long problemId) {
		
		List<Solution> solutionList = new ArrayList<Solution>();
		
		Cursor cursor = sqliteDatabase.rawQuery("select solution_id, solution_server_id ,  solution_text, " +
				"solution_user from Solutions where problem_server_id=?" , new String[]{ String.valueOf(problemId)} );
		
		while(null !=cursor && cursor.moveToNext()) {
			solutionList.add(new Solution(cursor.getLong(0), cursor.getLong(1), problemId, cursor.getString(2), cursor.getString(3)));
		}
		cursor.close();
		return solutionList;
	}
	
	public String getFirstAnnotateText(String problemId){
		
		String string = null;
		
		Cursor cursor = sqliteDatabase.rawQuery("select annotated_text from Annotations where problem_id=?", new String[]{ problemId });
		while(null != cursor && cursor.moveToNext()){
			
			string = cursor.getString(0);
			if(string != null && string.length() > 0 ) {
				cursor.close();	
				break;
			}
		}
		return string;
	}
	
	public void updateLocalImagePath(Annotate annotate){
		Cursor countCursor = sqliteDatabase.rawQuery("select count(*) from Annotations where annotation_server_id=?" , new String[]{ String.valueOf(annotate.getServerId())} );
		
		if((null !=countCursor && countCursor.moveToNext())) {
			int count = countCursor.getInt(0);
			
			if(count > 0) {
				SQLiteStatement sqliteStatement = sqliteDatabase.compileStatement("update Annotations" +
				" set annotate_local_path=? where annotation_server_id=?");
				sqliteStatement.bindString(1, annotate.getLocalImagePath());
				sqliteStatement.bindLong(2, annotate.getServerId());
				sqliteStatement.execute();
				sqliteStatement.close();
			} 
		}
		countCursor.close();
	}
	
	public String getStudyGroupNameFromServerId(long serverId){
		String string = null;
		Cursor cursor = sqliteDatabase.rawQuery("select name from StudyGroups where studygroup_server_id=?", 
				new String[]{ String.valueOf(serverId)});
		while(null != cursor && cursor.moveToNext()){
			string = cursor.getString(0);
			if(string != null && string.length() > 0){
				cursor.close();
				break;
			}
		}
		return string;
	}
	
	private void insertCategory(String categoryName) {
		SQLiteStatement sqliteStatement = sqliteDatabase
				.compileStatement("insert into Categories (name) Values (?)");
		sqliteStatement.bindString(1, categoryName);
		sqliteStatement.execute();
		sqliteStatement.close();
	}
	
	public void insertStudyGroup(long studygroup_server_id,String studyGroupName) {
		Cursor countCursor = sqliteDatabase.rawQuery("select count(*) from StudyGroups where studygroup_server_id=?" , new String[]{ String.valueOf(studygroup_server_id)} );
		
		if((null !=countCursor && countCursor.moveToNext())) {
			int count = countCursor.getInt(0);
			
			if(count < 1) {
				SQLiteStatement sqliteStatement = sqliteDatabase.compileStatement("insert into StudyGroups (studygroup_server_id,name) Values(?,?)");
				sqliteStatement.bindLong(1, studygroup_server_id);
				sqliteStatement.bindString(2, studyGroupName);
				sqliteStatement.execute();
				sqliteStatement.close();
			} 
		}
		countCursor.close();
	}
}
