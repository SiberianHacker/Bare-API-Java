package ru.bareapi.lib;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

import ru.bareapi.lib.exception.BareAPIException;
import ru.bareapi.lib.exception.ReadKeyException;
import ru.bareapi.lib.utils.BareImageUtils;
import ru.bareapi.lib.utils.BareMiscUtils;

public class BareAPI {

	private String API_SITE;
	private String API_KEY;
	private boolean resize;
	private int responseDelay = 800;

	/**
	 * Инициализация Bare API
	 * @return Возвращает true, если инициализация успешна
	 * @throws IOException
	 */
	public boolean initBareAPI() throws IOException {
		boolean isInited = false;
		String urlString = "https://raw.githubusercontent.com/SiberianHacker/NitroGen-News/refs/heads/main/APIHost.txt";
		@SuppressWarnings("deprecation")
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = reader.readLine();
		if (line != null && !line.isEmpty()) {
			this.API_SITE = line;
			isInited = true;
		}
		reader.close();
		return isInited;
	}

	/**
	 * Метод позволяющий установить ключ Bare API
	 * @param apiKey Ключ для Bare API
	 */
	public void setKey(String apiKey) {
		this.API_KEY = apiKey;
	}
	
	/**
	 * Если установить true, будет менять размер
	 * изображений при отправке в 250x150
	 * @param resize
	 */
	public void setResize(boolean resize) {
		this.resize = resize;
	}
	
	/**
	 * Установить задержку на получение ответа от Bare API.
	 * (В милисекундах)
	 * По умолчанию 800.
	 * Минимальное значение 300.
	 * @param delay
	 */
	public void setDelay(int delay) {
		this.responseDelay = (delay < 300) ? 800 : delay;
	}
	
	/**
	 * Метод позволяющий установить ключ Bare API
	 * из файла
	 * @param apiKeyFile File с ключом
	 */
	public void setKeyFromFile(File apiKeyFile) {
		try {
			this.API_KEY = BareMiscUtils.readApiKeyFromFile(apiKeyFile);
		} catch (ReadKeyException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Метод для решения капчи из файла,
	 * чтобы решить капчу, укажите имя файла
	 * @param filename Имя файла с капчей, которую нужно решить с Bare API
	 * @return Возвращает ответ на капчу
	 * @throws Exception
	 */
	public String solveCaptcha(String filename) throws Exception {
		File captchaFile = new File(filename);
		return this.solveCaptcha(captchaFile);
	}
	
	/**
	 * Метод для решения капчи из File
	 * @param captchaFile Входной File, например new File("captcha.png")
	 * @return Возвращает ответ на капчу
	 * @throws Exception
	 */
	public String solveCaptcha(File captchaFile) throws Exception {
		BufferedImage image = ImageIO.read(captchaFile);
		return this.solveCaptcha(image);
	}
	
	/**
	 * Метод для решения капчи из BufferedImage. Будет работать
	 * только если Bare API инициализирован при помощи initBareAPI().
	 * @param captchaImage Входяшее BufferedImage капчи
	 * @return Возвращает ответ на капчу
	 * @throws Exception
	 */
	public String solveCaptcha(BufferedImage captchaImage) throws Exception {
		if (checks())
			return null;
		
		BufferedImage inputImage = captchaImage;
		
		if (this.resize) {
			inputImage = BareImageUtils.resizeImage(inputImage);
		}
		
		String postData = "key=" + URLEncoder.encode(this.API_KEY, "UTF-8") + "&method=base64" + "&body=" + URLEncoder.encode(BareImageUtils.imageToBase64(captchaImage), "UTF-8");
		String postResponse = sendPost(this.API_SITE + "in.php", postData);
		
		Thread.sleep(this.responseDelay);

		String captchaId;
		try {
			captchaId = postResponse.split("\\|")[1].trim();
		} catch (Exception e) {
			return "CAPCHA_NO_READY";
		}

		String getUrl = this.API_SITE + "res.php?" + "key=" + URLEncoder.encode(this.API_KEY, "UTF-8") + "&action=get" + "&id=" + URLEncoder.encode(captchaId, "UTF-8");
		String getResponse = sendGet(getUrl);
		String answer;
		
		try {
			answer = getResponse.split("\\|")[1].trim();
		} catch (Exception e) {
			answer = "";
		}

		return answer;
	}
	
	// Проверка на настроенность софта.
	private boolean checks() {
		if (this.API_SITE == null) {
			System.out.println("Bare API не инициализирован. Используйте метод initBareAPI перед началом работы.");
			return true;
		} else if (this.API_KEY == null) {
			System.out.println("Установите ключ Bare API с помощью метода setKey.");
			return true;
		} else {
			return false;
		}
	}
	
	private String sendPost(String url, String postData) throws BareAPIException, IOException {
	    @SuppressWarnings("deprecation")
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
	    conn.setRequestMethod("POST");
	    conn.setDoOutput(true);
	    conn.setConnectTimeout(15000);
	    conn.setReadTimeout(15000);
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    conn.setRequestProperty("Charset", "UTF-8");

	    try (OutputStream os = conn.getOutputStream()) {
	        os.write(postData.getBytes(StandardCharsets.UTF_8));
	    }

	    return readResponse(conn);
	}

	private String sendGet(String url) throws BareAPIException, IOException {
	    @SuppressWarnings("deprecation")
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
	    conn.setRequestMethod("GET");
	    conn.setConnectTimeout(15000);
	    conn.setReadTimeout(15000);

	    return readResponse(conn);
	}

	private String readResponse(HttpURLConnection conn) throws BareAPIException, IOException {
	    int status = conn.getResponseCode();

	    if (status >= 400) {
	        String errorBody = readStream(conn.getErrorStream());
	        String message = switch (status) {
	            case 404  -> "API не находится по этому адресу.";
	            case 403  -> "Нет доступа для вашего ключа или IP.";
	            case 500  -> "API не смогла решить капчу";
	            case 405  -> "Неверный запрос (Сообщите администратору)";
	            case 502  -> "API временно недоступна";
	            default   -> "Ошибка API: " + status;
	        };
	        
	        throw new BareAPIException(status, message + "\n" + errorBody);
	    }

	    return readStream(conn.getInputStream());
	}

	private String readStream(InputStream stream) throws IOException {
	    if (stream == null) return "";

	    try (BufferedReader reader = new BufferedReader(
	            new InputStreamReader(stream, StandardCharsets.UTF_8))) {
	        StringBuilder sb = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line).append('\n');
	        }
	        return sb.toString().trim();
	    }
	}
}
