package in.gym.app.Dto;

public class ApiResponse {

    private boolean success;
    private String message;
    private int status;

    private String accessToken;
    private String refreshToken;
    private int expiresIn;

    public ApiResponse(boolean success, String message, int status) {
        this.success = success;
        this.message = message;
        this.status = status;
    }

    public ApiResponse(boolean success, String message, int status,
                       String accessToken, String refreshToken, int expiresIn) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public int getStatus() {
		return status;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

    
    
}
