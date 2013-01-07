package com.example.afilelist;

import java.io.File;
import java.io.FileFilter;

public enum SelectMode implements FileFilter {
	OPEN_FILE() {
		@Override
		public boolean isOk(File file) {
			return file.canRead() && file.isFile();
		}

		@Override
		public boolean accept(File pathname) {
			// show all files
			return true;
		}
	},
	OPEN_FOLDER() {
		@Override
		public boolean isOk(File file) {
			return file.isDirectory();
		}

		@Override
		public boolean accept(File pathname) {
			// accept folders only
			return pathname.isDirectory();
		}
	},
	SAVE_FILE() {
		@Override
		public boolean accept(File pathname) {
			// accept files and folders... everything
			return true;
		}

		@Override
		public boolean isOk(File file) {
			File parentFile = file.getParentFile();
			if (parentFile != null) {
				return parentFile.canWrite();
			}
			return true;
		}
	};
	public abstract boolean isOk(File file);

}
