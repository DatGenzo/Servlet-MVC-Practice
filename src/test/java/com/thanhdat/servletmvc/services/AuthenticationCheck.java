package com.thanhdat.servletmvc.services;

import java.io.Console;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.services.impl.UserServiceImpl;

public final class AuthenticationCheck {

    private AuthenticationCheck() {
    }

    public static void main(String[] args) {
        Credentials credentials = readCredentials();

        try {
            UserService userService =
                    new UserServiceImpl();

            Optional<User> authenticatedUser =
                    userService.authenticate(
                            credentials.username(),
                            credentials.password()
                    );

            if (authenticatedUser.isPresent()) {
                User user = authenticatedUser.get();

                System.out.println();
                System.out.println(
                        "Đăng nhập thành công."
                );
                System.out.println(
                        "Username: " + user.getUsername()
                );
                System.out.println(
                        "Họ tên: " + user.getFullName()
                );
                System.out.println(
                        "Vai trò: " + user.getRole()
                );
            } else {
                System.out.println();
                System.out.println(
                        "Username hoặc mật khẩu không đúng."
                );
            }
        } catch (DataAccessException exception) {
            System.err.println();
            System.err.println(
                    "Không thể truy cập database."
            );
            exception.printStackTrace();
        } finally {
            Arrays.fill(
                    credentials.password(),
                    '\0'
            );
        }
    }

    private static Credentials readCredentials() {
        Console console = System.console();

        if (console != null) {
            String username = console.readLine(
                    "Username: "
            );

            char[] password = console.readPassword(
                    "Password: "
            );

            return new Credentials(username, password);
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        char[] password = scanner.nextLine().toCharArray();

        return new Credentials(username, password);
    }

    private record Credentials(
            String username,
            char[] password
    ) {
    }
}