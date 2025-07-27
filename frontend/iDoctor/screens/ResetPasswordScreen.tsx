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
import LoadingComponent from "../components/LoadingComponent";
import {
  NavigationProp,
  RouteProp,
  useNavigation,
  useRoute,
} from "@react-navigation/native";
import { RootStackParamList } from "../App";

type ResetPasswordRouteProp = RouteProp<RootStackParamList, "ResetPassword">;

export default function ResetPasswordScreen() {
  const [invalid, setInvalid] = useState<boolean>(false);
  const [password, setPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const navigation = useNavigation<NavigationProp<RootStackParamList>>();
  const route = useRoute<ResetPasswordRouteProp>();

  async function handleResetPassword() {
    try {
      setIsLoading(true);
      setInvalid(false);
      const response = await fetch(
        `http://localhost:9090/api/userx/reset-password/${route.params.email}`,
        {
          method: "PUT",
          body: JSON.stringify({
            password: password,
            confirmPassword: confirmPassword,
          }),
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        setInvalid(false);
        throw new Error("error with response status code: " + response.status);
      }

      setIsLoading(false);
      navigation.navigate("SignIn");
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
        <View style={styles.inputContainer}>
          <TextInput
            style={
              invalid ? [styles.input, { borderColor: "red" }] : styles.input
            }
            placeholder="New Password"
            placeholderTextColor={invalid ? "red" : "grey"}
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            secureTextEntry
            onChangeText={setPassword}
            value={password}
          />

          <TextInput
            style={
              invalid ? [styles.input, { borderColor: "red" }] : styles.input
            }
            placeholder="Confirm Password"
            placeholderTextColor={invalid ? "red" : "grey"}
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            secureTextEntry
            onChangeText={setConfirmPassword}
            value={confirmPassword}
          />
          {invalid && (
            <Text style={{ color: "red" }}>
              Password invalid or you didn't confirm your email.
            </Text>
          )}
        </View>
        <Pressable
          style={({ pressed }) =>
            pressed ? [styles.pressed, styles.signInBtn] : styles.signInBtn
          }
          onPress={handleResetPassword}
        >
          <Text style={{ color: "white", fontWeight: "bold" }}>
            Change Password
          </Text>
        </Pressable>
      </View>
    </TouchableWithoutFeedback>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    backgroundColor: "white",
    gap: 20,
  },

  inputContainer: {
    width: "100%",
    alignItems: "center",
    justifyContent: "center",
    gap: 20,
    marginTop: 50,
    marginBottom: 20,
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

  signInBtn: {
    backgroundColor: "black",
    alignItems: "center",
    justifyContent: "center",
    width: "90%",
    borderRadius: 25,
    height: 50,
  },
});
