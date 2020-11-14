with import <nixpkgs> {};

stdenv.mkDerivation {
  name = "jbert";

  buildInputs = with pkgs; [
    figlet lolcat # banner printing on enter

    ansible
    cantata
    gradle
    openjdk11
    micronaut
    mpc_cli
  ];

  shellHook = ''
    figlet $name | lolcat --freq 0.5
  '';
}

