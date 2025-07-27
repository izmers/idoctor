import {
  Button,
  Keyboard,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  TouchableWithoutFeedback,
  View,
} from "react-native";
import ScreenTitleComponent from "../components/ScreenTitleComponent";
import { useContext, useState } from "react";
import { AuthContext } from "../store/auth-context";
import LoadingComponent from "../components/LoadingComponent";
import { handleLogin } from "../util/login";
import { NavigationProp, useNavigation } from "@react-navigation/native";
import { RootStackParamList } from "../App";

interface Credentials {
  username: string;
  password: string;
}

export default function SignInScreen() {
  const [credentials, setCredentials] = useState<Credentials>({
    username: "",
    password: "",
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [invalid, setInvalid] = useState<boolean>(false);
  const authCtx = useContext(AuthContext);
  const navigation = useNavigation<NavigationProp<RootStackParamList>>();

  function handleChangeCredentials(type: string, value: string): void {
    setCredentials((prev) => {
      return { ...prev, [type]: value };
    });
  }

  async function handleLoginProcess(): Promise<void> {
    await handleLogin(
      credentials,
      authCtx,
      setIsLoading,
      navigation,
      setInvalid
    );
  }

  if (isLoading) {
    return <LoadingComponent />;
  }

  return (
    <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
      <View style={styles.container}>
        <ScreenTitleComponent isMainTitle={false} subtitle="Sign in" />

        <View style={styles.inputContainer}>
          <TextInput
            style={
              invalid ? [styles.input, { borderColor: "red" }] : styles.input
            }
            placeholder="Username"
            placeholderTextColor={invalid ? "red" : "grey"}
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            onChangeText={(value) => handleChangeCredentials("username", value)}
            value={credentials.username}
          />

          <TextInput
            style={
              invalid ? [styles.input, { borderColor: "red" }] : styles.input
            }
            placeholder="Password"
            placeholderTextColor={invalid ? "red" : "grey"}
            returnKeyType="default"
            autoCorrect={false}
            autoComplete="off"
            autoCapitalize="none"
            onSubmitEditing={Keyboard.dismiss}
            secureTextEntry
            onChangeText={(value) => handleChangeCredentials("password", value)}
            value={credentials.password}
          />
          {invalid && (
            <Text style={{ color: "red" }}>Invalid username or password</Text>
          )}

          <Button
            title="Forgot your password?"
            onPress={() => navigation.navigate("VerifyEmail")}
          />
        </View>

        <Pressable
          style={({ pressed }) =>
            pressed ? [styles.pressed, styles.signInBtn] : styles.signInBtn
          }
          onPress={handleLoginProcess}
        >
          <Text style={{ color: "white", fontWeight: "bold" }}>SIGN IN</Text>
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
