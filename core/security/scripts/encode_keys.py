#!/usr/bin/env python3
"""
API Key XOR Encoder for Native Library

This script encodes API keys using XOR obfuscation for use in the
native-keys.cpp file. The encoded keys are harder to extract from
the compiled binary.

Usage:
    python encode_keys.py "your_api_key" 0x5A
    python encode_keys.py --decode "0x38, 0x31, 0x1a" 0x5A

Examples:
    # Encode an API key with XOR key 0x5A
    python encode_keys.py "bk_fake_api_key_12345_demo" 0x5A

    # Encode a secret key with XOR key 0x7F
    python encode_keys.py "sk_fake_secret_key_67890_demo" 0x7F

    # Decode existing encoded bytes
    python encode_keys.py --decode "0x38, 0x31, 0x1a, 0x3c" 0x5A

Output can be directly pasted into native-keys.cpp

SECURITY WARNING:
- Never commit real production keys to version control!
- Use this script locally or in CI/CD pipelines
- Store the output securely
"""

import sys
import argparse


def encode_key(key: str, xor_key: int) -> list[str]:
    """
    Encode a string using XOR with the given key.
    
    Args:
        key: The plaintext key to encode
        xor_key: The XOR key (0-255)
    
    Returns:
        List of hex strings representing encoded bytes
    """
    encoded = []
    for char in key:
        encoded_byte = ord(char) ^ xor_key
        encoded.append(f"0x{encoded_byte:02x}")
    return encoded


def decode_key(encoded_hex: str, xor_key: int) -> str:
    """
    Decode XOR-encoded hex bytes back to plaintext.
    
    Args:
        encoded_hex: Comma-separated hex values (e.g., "0x38, 0x31, 0x1a")
        xor_key: The XOR key used for encoding
    
    Returns:
        Decoded plaintext string
    """
    # Parse hex values
    hex_values = [x.strip() for x in encoded_hex.split(',')]
    decoded_chars = []
    
    for hex_val in hex_values:
        if hex_val:
            byte_val = int(hex_val, 16)
            decoded_char = chr(byte_val ^ xor_key)
            decoded_chars.append(decoded_char)
    
    return ''.join(decoded_chars)


def format_cpp_array(encoded: list[str], line_width: int = 8) -> str:
    """
    Format encoded bytes as a C++ vector initializer.
    
    Args:
        encoded: List of hex strings
        line_width: Number of bytes per line
    
    Returns:
        Formatted C++ array initializer
    """
    lines = []
    for i in range(0, len(encoded), line_width):
        chunk = encoded[i:i + line_width]
        lines.append("        " + ", ".join(chunk))
    
    return ",\n".join(lines)


def main():
    parser = argparse.ArgumentParser(
        description="XOR encode/decode API keys for native library",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__
    )
    
    parser.add_argument(
        "key",
        help="The key to encode, or hex bytes to decode (with --decode)"
    )
    
    parser.add_argument(
        "xor_key",
        help="XOR key in hex (e.g., 0x5A) or decimal (e.g., 90)"
    )
    
    parser.add_argument(
        "--decode", "-d",
        action="store_true",
        help="Decode hex bytes back to plaintext"
    )
    
    parser.add_argument(
        "--verify", "-v",
        action="store_true",
        help="Verify encoding by decoding back"
    )
    
    args = parser.parse_args()
    
    # Parse XOR key
    xor_key_str = args.xor_key
    if xor_key_str.startswith("0x") or xor_key_str.startswith("0X"):
        xor_key = int(xor_key_str, 16)
    else:
        xor_key = int(xor_key_str)
    
    if not 0 <= xor_key <= 255:
        print("Error: XOR key must be between 0 and 255", file=sys.stderr)
        sys.exit(1)
    
    if args.decode:
        # Decode mode
        decoded = decode_key(args.key, xor_key)
        print(f"Decoded string: {decoded}")
    else:
        # Encode mode
        key = args.key
        encoded = encode_key(key, xor_key)
        
        print(f"Original key: {key}")
        print(f"Key length: {len(key)} characters")
        print(f"XOR key: 0x{xor_key:02X}")
        print()
        print("C++ vector initializer:")
        print("const std::vector<char> YOUR_KEY_ENCODED = {")
        print(format_cpp_array(encoded))
        print("};")
        print()
        print(f"Single line: {{ {', '.join(encoded)} }}")
        
        if args.verify:
            # Verify by decoding
            hex_str = ", ".join(encoded)
            verified = decode_key(hex_str, xor_key)
            print()
            if verified == key:
                print("✓ Verification passed: decoded matches original")
            else:
                print(f"✗ Verification FAILED!")
                print(f"  Original: {key}")
                print(f"  Decoded:  {verified}")
                sys.exit(1)


if __name__ == "__main__":
    main()

