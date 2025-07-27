import { NavigationProp, useNavigation } from "@react-navigation/native";
import { useState } from "react";
import {
  Keyboard,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  TouchableWithoutFeedback,
  View,
} from "react-native";
import { RootStackParamList } from "../App";
import LoadingComponent from "../components/LoadingComponent";

export default function ResetPasswordEmailVerificationScreen() {
  const [emailError, setEmailError] = useState<boolean>(false);
  const [email, setEmail] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const navigation = useNavigation<NavigationProp<RootStackParamList>>();

  async function handleEmailVerification() {
    try {
      setIsLoading(true);
      setEmailError(false);
      const response = await fetch(
        `http://localhost:9090/api/email/forgot-password/${email}`,
        {
          method: "POST",
        }
      );

      if (!response.ok) {
        setEmailError(true);
        throw new Error("error with response status code: " + response.status);
      }

      setIsLoading(false);
      navigation.navigate("ResetPassword", { email: email });
    } catch (error) {
      if (error instanceof Error) {
        console.error(error.message);
      } else {
        console.error("An unknown error occurred", error);
      }
      setIsLoading(false);
    }
  }

  if (isLoading) {
    return <LoadingComponent />;
  }

  return (
    <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
      <View style={styles.container}>
        <View
          style={{
            width: "100%",
            justifyContent: "space-evenly",
            alignItems: "center",
            height: "35%",
          }}
        >
          <TextInput
            style={
              emailError ? [styles.input, { borderColor: "red" }] : styles.input
            }
            placeholder={
              emailError ? "Email address not found" : "Email address"
            }
            placeholderTextColor={emailError ? "red" : "grey"}
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            onChangeText={setEmail}
            value={email}
          />
          <Text>
            We will send you a confirmation email. Please follow the
            instructions.
          </Text>
          <Pressable
            style={({ pressed }) =>
              pressed ? [styles.pressed, styles.signUpBtn] : styles.signUpBtn
            }
            onPress={handleEmailVerification}
          >
            <Text style={{ color: "white", fontWeight: "bold" }}>Continue</Text>
          </Pressable>
        </View>
      </View>
    </TouchableWithoutFeedback>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: "white",
  },
  input: {
    width: "90%",
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
