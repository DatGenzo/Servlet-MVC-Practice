package com.thanhdat.servletmvc.utils;

import java.io.Console;
import java.util.Arrays;
import java.util.Scanner;

public final class PasswordHashGenerator {

    private PasswordHashGenerator() {
    }

    public static void main(String[] args) {
        char[] password = readPassword();

        try {
            String passwordHash = PasswordUtils.hash(password);

            System.out.println();
            System.out.println("BCrypt hash:");
            System.out.println(passwordHash);
            System.out.println("Độ dài hash: " + passwordHash.length());
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private static char[] readPassword() {
        Console console = System.console();

        if (console != null) {
            return console.readPassword(
                    "Nhập mật khẩu cho tài khoản admin: "
            );
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print(
                "Nhập mật khẩu cho tài khoản admin: "
        );

        return scanner.nextLine().toCharArray();
    }
}