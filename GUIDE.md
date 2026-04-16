# Guide

## Overview
The Checksum Calculator is a command-line tool for computing and verifying file checksums. It helps ensure data integrity by detecting modifications, deletions, or additions in a directory.

## Features
- Compute checksums for files and directories
- Verify file integrity against precomputed checksums
- Handle symbolic links and shortcuts
- Display progress with a visual indicator
- Support pausing and resuming operations
- Generate reports in multiple formats

---

## Command-Line Usage

### 1. Compute Checksums
```
-m=calculate -p=<path_in_filesystem> -a=<md5/sha256> -c=<checksums_path .txt/.xml>
```
- `-p` : Specifies the file or directory to process (default: current folder).
- `-a` : Defines the hashing algorithm to use (MD5, SHA256, default: SHA256).
- `-c` : Path to save computed checksums (default: `checksums.txt`).

Example:
```
-m=calculate -p=/home/user/documents -a=sha256 -c=checksums.xml
```

### 2. Verify Checksums
```
-m=verify -p=<path_in_filesystem> -a=<md5/sha256> -c=<checksums_path .txt/.xml> -t=<path_to_save_verification>
```
- `-t` : Specifies where to save the verification results (default: console).

Example:
```
-m=verify -p=/home/user/documents -a=sha256 -c=checksums.xml -t=verified.txt
```

### 3. Handling Symbolic Links
```
-m=calculate -nfl
```
- `-nfl` : Disables following symbolic links (default: follows links).

### 4. Pause & Resume
- **Pause:**
  ```
  -m=pause
  ```
- **Resume:**
  ```
  -m=resume
  ```

### 5. Progress Indicator
Example output:
```
Processing: file3.txt [#########.....] 60%  (Estimated time: 30s)
```

---
## Some file directory
```
/home/user/documents/
├── file1.txt 
├── file2.pdf 
├── folderA/ 
│   ├── fileA1.txt 
│   ├── fileA2.log  
│   ├── symlink_to_folderB -> ../folderB/
│   └── shortcut_to_fileB1.lnk -> ../folderB/fileB1.txt
├── folderB/ 
│   ├── fileB1.txt 
│   ├── fileB2.csv 
│   └── nestedFolder/
│       ├── fileN1.docx
│       ├── fileN2.json
│       └── anotherSymlink -> ../../folderA/
└── checksums.txt
```
## Example Workflows

![image](https://github.com/user-attachments/assets/6916f67b-edb4-43cb-bc7e-862544f281d7)


### if a node is in green, program will signal that the progress of the file is saved and has already been processed, at the end checksums file will contain all checksums of the leaves and the shortcuts raw file

### Example 1
```
-m=calculate -p=C:..\..\ -a=md5 -c=checks/checksums.xml
-m=pause (Pausing at 20%)
-m=verify -p=C:..\..\someFolder -a=md5 -c=checks/checksums.xml -t=verificated.txt
-m=pause (Pausing at 39%)
-m=resume
```

### Example 2 (Skipping Symbolic Links)
```
-m=calculate -p=C:..\..\ -a=md5 -c=checks/checksums.txt -nfl
-m=pause (Pausing at 20%)
-m=verify -p=C:..\..\someFolder -a=md5 -c=checks/checksums.txt -t=verificated.txt -nfl
-m=pause (Pausing at 39%)
-m=resume
```

### Example 3 (Console Verification)
```
-m=calculate -p=C:..\..\someFolder -a=md5 -c=checks/checksums.xml
-m=pause (Pausing at 20%)
-m=verify -p=C:..\..\someFolder -a=md5 -c=checks/checksums.xml
-m=pause (Pausing at 39%)
-m=resume
```

### Example 4 (Default Folder)
```
-m=calculate -a=md5 -c=checks/checksums.xml
-m=pause (Pausing at 20%)
-m=verify -a=md5 -c=checks/checksums.xml
-m=pause (Pausing at 39%)
-m=resume
```

### Example 5 (Default Checksum File)
```
-m=calculate -a=md5
-m=pause (Pausing at 20%)
-m=verify -a=md5
-m=pause (Pausing at 39%)
-m=resume
```

### Example 6 (Default Algorithm: SHA256)
```
-m=calculate
-m=pause (Pausing at 20%)
-m=verify
-m=pause (Pausing at 39%)
-m=resume
```

---

## Creating Symbolic Links & Shortcuts

### Creating a Symlink (Windows)
1. Open CMD as Administrator.
2. Run:
   ```
   mklink /D "path_for_symlink" "target_path"
   ```

### Creating a Shortcut (Manual)
1. Right-click the target file/folder.
2. Select "Create Shortcut".
3. Move the shortcut to the desired location.

---

## Error Handling
| Error | Cause | Solution |
|-------|-------|----------|
| File not found | Incorrect path | Check the file path and retry |
| Permission denied | Insufficient privileges | Use `sudo` or change file permissions |
| Algorithm not supported | Invalid `-a` option | Use `md5` or `sha256` |

---

## Conclusion
The Checksum Calculator provides a powerful way to maintain data integrity across files and directories. By using this tool, you can efficiently track changes, detect anomalies, and ensure security in your file system.
