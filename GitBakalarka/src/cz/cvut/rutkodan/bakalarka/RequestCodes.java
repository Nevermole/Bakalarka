package cz.cvut.rutkodan.bakalarka;

public enum RequestCodes {
	ADD_NEW_CAMERA(1), EDIT_CAMERA(2), MANAGE_CAMERAS(3);
	private int number;

	private RequestCodes(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}
}
