package ru.bareapi.lib.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ru.bareapi.lib.exception.ReadKeyException;

public class BareMiscUtils {
	
	public static String readApiKeyFromFile(File keyFile) throws ReadKeyException {
	    String apiKey;

	    try (BufferedReader reader = new BufferedReader(new FileReader(keyFile))) {
	        apiKey = reader.readLine();
	    } catch (IOException e) {
	        throw new ReadKeyException("Не удалось прочесть файл с ключом Bare API: " + e.getMessage());
	    }
	    
	    if (apiKey == null || apiKey.length() != 25) {
	        throw new ReadKeyException("Неверный ключ Bare API");
	    }

	    return apiKey;
	}
}
