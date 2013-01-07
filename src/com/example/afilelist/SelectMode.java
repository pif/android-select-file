package com.example.afilelist;

import java.io.File;
import java.io.FileFilter;

public enum SelectMode {
	OPEN_FILE(new FileFilter(){
		@Override
		public boolean accept(File pathname) {
			// accept files and folders... everything
			return true;
		}}) {
			@Override
			public boolean isOk(File file) {
				return file.canRead() && file.isFile();
			}
		}, 
	OPEN_FOLDER(new FileFilter(){
		@Override
		public boolean accept(File pathname) {
			// accept folders only
			return pathname.isDirectory();
		}}) {
			@Override
			public boolean isOk(File file) {
				return file.isDirectory();
			}
		},  
	SAVE_FILE(new FileFilter(){
		@Override
		public boolean accept(File pathname) {
			// accept files and folders... everything
			return true;
		}}) {
			@Override
			public boolean isOk(File file) {
				return file.canWrite();
			}
		}, ;

	private SelectMode(FileFilter filter) {
		this.fileFilter = filter;
	}
	
	private FileFilter fileFilter;
	
	public FileFilter getFileFilter() {
		return fileFilter;
	}
	
	public abstract boolean isOk(File file);
}
