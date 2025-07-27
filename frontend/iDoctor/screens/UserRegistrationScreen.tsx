import {
  StyleSheet,
  View,
  TextInput,
  Text,
  Keyboard,
  TouchableWithoutFeedback,
  Pressable,
  Button,
} from "react-native";
import ScreenTitleComponent from "../components/ScreenTitleComponent";
import { NavigationProp, useNavigation } from "@react-navigation/native";
import { RootStackParamList } from "../App";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { useContext, useState } from "react";
import { AuthContext } from "../store/auth-context";
import LoadingComponent from "../components/LoadingComponent";
import { handleLogin } from "../util/login";
import { API_URL } from "../util/requests";

interface Registration {
  email: string;
  fullName: string;
  username: string;
  password: string;
  confirmPassword: string;
  country: string;
  city: string;
  zip: string;
}

interface FieldErrors {
  email: boolean;
  password: boolean;
  confirmPassword: boolean;
  empty: boolean;
}

export default function UserRegistrationScreen() {
  const [registration, setRegistration] = useState<Registration>({
    email: "",
    fullName: "",
    username: "",
    password: "",
    confirmPassword: "",
    country: "",
    city: "",
    zip: "",
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({
    email: false,
    password: false,
    confirmPassword: false,
    empty: false,
  });
  const authCtx = useContext(AuthContext);
  const navigation = useNavigation<NavigationProp<RootStackParamList>>();

  function handleRegistration(type: string, value: string): void {
    setRegistration((prev) => {
      return { ...prev, [type]: value };
    });
  }

  async function handleSubmitRegistration(): Promise<void> {
    try {
      setIsLoading(true);
      setFieldErrors({
        email: false,
        password: false,
        confirmPassword: false,
        empty: false,
      });

      const response = await fetch(`${API_URL}/api/userx/register`, {
        method: "POST",
        body: JSON.stringify(registration),
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        const resData = await response.json();
        const message: string[] = resData.message;

        const errorMap: { [key: string]: keyof FieldErrors } = {
          "Password is too weak or invalid.": "password",
          "muss eine korrekt formatierte E-Mail-Adresse sein": "email",
          "The password and confirmation password of user do not match.":
            "confirmPassword",
          "darf nicht null sein": "empty",
        };

        const updatedErrors: Partial<FieldErrors> = {};

        Object.entries(errorMap).forEach(([key, field]) => {
          if (message.includes(key)) {
            updatedErrors[field] = true;
          }
        });

        if (Object.keys(updatedErrors).length > 0) {
          setFieldErrors((prev) => ({ ...prev, ...updatedErrors }));
        }

        throw new Error("Error with response status code: " + response.status);
      }
    } catch (error) {
      if (error instanceof Error) {
        console.error(error.message);
      } else {
        console.error("An unknown error occurred", error);
      }
      setIsLoading(false);
      return;
    }

    setIsLoading(false);
    await handleLogin(
      { username: registration.username, password: registration.password },
      authCtx,
      setIsLoading,
      navigation
    );
  }

  if (isLoading) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <LoadingComponent />
      </View>
    );
  }

  return (
    <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
      <KeyboardAwareScrollView style={{ backgroundColor: "white" }}>
        <ScreenTitleComponent isMainTitle={false} subtitle="Sign up" />
        <View style={styles.inputContainer}>
          <TextInput
            style={
              fieldErrors.email ||
              (fieldErrors.empty && registration.email.length === 0)
                ? [styles.input, { borderColor: "red" }]
                : styles.input
            }
            placeholder={
              fieldErrors.email ||
              (fieldErrors.empty && registration.email.length === 0)
                ? "Email is not correctly formatted or empty!"
                : "Email address"
            }
            placeholderTextColor={
              fieldErrors.email ||
              (fieldErrors.empty && registration.email.length === 0)
                ? "red"
                : "gray"
            }
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            onChangeText={(value) => handleRegistration("email", value)}
            value={registration.email}
          />
          <TextInput
            style={
              fieldErrors.empty && registration.fullName.length === 0
                ? [styles.input, { borderColor: "red" }]
                : styles.input
            }
            placeholder={
              fieldErrors.empty && registration.fullName.length === 0
                ? "Please enter your full name!"
                : "Full name"
            }
            placeholderTextColor={
              fieldErrors.empty && registration.fullName.length === 0
                ? "red"
                : "grey"
            }
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            onChangeText={(value) => handleRegistration("fullName", value)}
            value={registration.fullName}
          />

          <TextInput
            style={
              fieldErrors.empty && registration.country.length === 0
                ? [styles.input, { borderColor: "red" }]
                : styles.input
            }
            placeholder={
              fieldErrors.empty && registration.country.length === 0
                ? "Please enter your country!"
                : "Country"
            }
            placeholderTextColor={
              fieldErrors.empty && registration.country.length === 0
                ? "red"
                : "grey"
            }
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            onChangeText={(value) => handleRegistration("country", value)}
            value={registration.country}
          />

          <View
            style={{
              width: "90%",
              flexDirection: "row",
              justifyContent: "space-between",
            }}
          >
            <TextInput
              style={
                fieldErrors.empty && registration.city.length === 0
                  ? [styles.inputCityZip, { borderColor: "red" }]
                  : styles.inputCityZip
              }
              placeholder={
                fieldErrors.empty && registration.city.length === 0
                  ? "Please enter your city!"
                  : "City"
              }
              placeholderTextColor={
                fieldErrors.empty && registration.city.length === 0
                  ? "red"
                  : "grey"
              }
              returnKeyType="default"
              autoCorrect={false}
              autoComplete="off"
              autoCapitalize="none"
              onSubmitEditing={Keyboard.dismiss}
              onChangeText={(value) => handleRegistration("city", value)}
              value={registration.city}
            />

            <TextInput
              style={
                fieldErrors.empty && registration.zip.length === 0
                  ? [styles.inputCityZip, { borderColor: "red" }]
                  : styles.inputCityZip
              }
              placeholder={
                fieldErrors.empty && registration.zip.length === 0
                  ? "Please enter your zip!"
                  : "Postal code"
              }
              placeholderTextColor={
                fieldErrors.empty && registration.zip.length === 0
                  ? "red"
                  : "grey"
              }
              returnKeyType="default"
              autoCorrect={false}
              autoComplete="off"
              autoCapitalize="none"
              onSubmitEditing={Keyboard.dismiss}
              onChangeText={(value) => handleRegistration("zip", value)}
              value={registration.zip}
            />
          </View>

          <TextInput
            style={
              fieldErrors.empty && registration.username.length === 0
                ? [styles.input, { borderColor: "red" }]
                : styles.input
            }
            placeholder={
              fieldErrors.empty && registration.username.length === 0
                ? "Please enter your username"
                : "Username"
            }
            placeholderTextColor={
              fieldErrors.empty && registration.username.length === 0
                ? "red"
                : "grey"
            }
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            onChangeText={(value) => handleRegistration("username", value)}
            value={registration.username}
          />

          <TextInput
            style={
              fieldErrors.password
                ? [styles.input, { borderColor: "red" }]
                : styles.input
            }
            placeholder={
              fieldErrors.password ? "Password is too weak!" : "Password"
            }
            placeholderTextColor={fieldErrors.password ? "red" : "grey"}
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            secureTextEntry
            onChangeText={(value) => handleRegistration("password", value)}
            value={registration.password}
          />

          {registration.password.length !== 0 && (
            <View style={{ width: "85%" }}>
              <Text style={{ color: "grey", fontSize: 12 }}>
                At least 12 characters, upper/lower case, a digit, and 2 special
                characters (@, #, !, $, ...)
              </Text>
            </View>
          )}

          <TextInput
            style={
              fieldErrors.confirmPassword
                ? [styles.input, { borderColor: "red" }]
                : styles.input
            }
            placeholder={
              fieldErrors.confirmPassword
                ? "Passwords do not match!"
                : "Confirm Password"
            }
            placeholderTextColor={fieldErrors.confirmPassword ? "red" : "grey"}
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            secureTextEntry
            onChangeText={(value) =>
              handleRegistration("confirmPassword", value)
            }
            value={registration.confirmPassword}
          />

          <Pressable
            style={({ pressed }) =>
              pressed ? [styles.pressed, styles.signUpBtn] : styles.signUpBtn
            }
            onPress={handleSubmitRegistration}
          >
            <Text style={{ color: "white", fontWeight: "bold" }}>SIGN UP</Text>
          </Pressable>

          <View
            style={{
              flexDirection: "row",
              justifyContent: "center",
              alignItems: "center",
              marginBottom: 25,
            }}
          >
            <Text style={{ fontSize: 18 }}>Already have an account?</Text>
            <Button
              title="Sign in"
              onPress={() => {
                navigation.navigate("SignIn");
              }}
            />
          </View>
        </View>
      </KeyboardAwareScrollView>
    </TouchableWithoutFeedback>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    backgroundColor: "white",
  },
  inputContainer: {
    width: "100%",
    alignItems: "center",
    justifyContent: "center",
    gap: 20,
    marginTop: 50,
  },
  input: {
    width: "90%",
    height: 55,
    padding: 10,
    borderWidth: 1,
    borderRadius: 25,
  },

  inputCityZip: {
    width: "49%",
    height: 55,
    padding: 10,
    borderWidth: 1,
    borderRadius: 25,
  },

  pressed: {
    opacity: 0.75,
  },

  signUpBtn: {
    backgroundColor: "black",
    alignItems: "center",
    justifyContent: "center",
    width: "90%",
    borderRadius: 25,
    height: 50,
  },
});
