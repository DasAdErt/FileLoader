package org.example;

import java.io.*;
import java.util.Random;

public class FileLoader {
    private static final String FILE_NAME = "security_department.txt";
    private static final String CODES_FILE_NAME = "access_codes.txt";
    private static final int ACCESS_CODE_LENGTH = 6;
    private static final int PROGRESS_BAR_LENGTH = 20;
    private static final int NUMBER_OF_EMPLOYEES = 10;

    public static void main(String[] args) {
        Thread generatorThread = new Thread(new CodeGenerator());
        generatorThread.start();

        try {
            generatorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Введите номер сотрудника: ");
        int employeeNumber = 0;
        try {
            employeeNumber = Integer.parseInt(br.readLine());
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }

        Employee employee = readEmployeeFromFile(employeeNumber);
        System.out.println("Сотрудник - " + employee.getName() + ", код доступа - " + employee.getAccessCode());
    }

    private static Employee readEmployeeFromFile(int employeeNumber) {
        File file = new File(FILE_NAME);
        int lineNumber = 1;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (lineNumber == employeeNumber) {
                    return new Employee(line, getAccessCode(line));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int getAccessCode(String employeeName) {
        File codesFile = new File(CODES_FILE_NAME);
        int accessCode = 0;

        try (RandomAccessFile raf = new RandomAccessFile(codesFile, "rw")) {
            byte[] buffer = new byte[ACCESS_CODE_LENGTH];
            long fileLength = codesFile.length();
            if (fileLength < ACCESS_CODE_LENGTH) {
                accessCode = generateAccessCode();
                raf.write(String.format("%06d", accessCode).getBytes());
            } else {
                long position = (fileLength / ACCESS_CODE_LENGTH - 1) * ACCESS_CODE_LENGTH;
                raf.seek(position);
                raf.read(buffer, 0, ACCESS_CODE_LENGTH);
                accessCode = Integer.parseInt(new String(buffer));
                raf.write(String.format("%06d", generateAccessCode()).getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return accessCode;
    }

    private static int generateAccessCode() {
        return new Random().nextInt((int) Math.pow(10, ACCESS_CODE_LENGTH));
    }

    private static void printProgressBar(int progress) {
        int numberOfBars = progress * PROGRESS_BAR_LENGTH / 100;
        String progressBar = "[" + "#".repeat(numberOfBars) + " ".repeat(PROGRESS_BAR_LENGTH - numberOfBars) + "]";
        System.out.print(progressBar + " " + progress + "%\r");
    }

    private static class CodeGenerator implements Runnable {
        @Override
        public void run() {
            File file = new File(FILE_NAME);

            if (file.exists()) {
                file.delete();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (int i = 1; i <= NUMBER_OF_EMPLOYEES; i++) {
                    String name = generateEmployeeName();
                    bw.write(name);
                    bw.newLine();
                    printProgressBar(i * 100 / NUMBER_OF_EMPLOYEES);
                }
                System.out.println("Генерация сотрудников завершена.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String generateEmployeeName() {
            String[] firstNames = {"Иван", "Петр", "Андрей", "Алексей", "Дмитрий", "Сергей", "Максим", "Артем", "Никита", "Кирилл"};
            String[] lastNames = {"Иванов", "Петров", "Сидоров", "Кузнецов", "Смирнов", "Попов", "Васильев", "Михайлов", "Федоров", "Ковалев"};

            Random random = new Random();
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            return firstName + " " + lastName;
        }
    }
}