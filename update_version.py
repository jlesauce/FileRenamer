#!/usr/bin/env python
import sys
import argparse
import re
import fileinput

VERSION_FILE = 'pom.xml'
FIND_VERSION_REGEX = '^\\s*<version>(.*)<\\/version>\\s* <!-- PROJECT.VERSION \\(Keep this tag\\) -->$'
VERSION_NUMBER_REGEX = '(\\d+\\.)?(\\d+\\.)?(\\*|\\d+)'


def main():
    arg_parser = create_argument_parser()
    arg_parser.parse_args(sys.argv[1:])

    old_version = retrieve_version_number(VERSION_FILE)
    new_version = increment_version_number(old_version)
    print('Increment project version from {} to {}'.format(old_version, new_version))
    replace_version_number_in_file(VERSION_FILE, old_version, new_version)
    print('Updated {} successfully'.format(VERSION_FILE))


def create_argument_parser():
    arg_parser = argparse.ArgumentParser(description='Update the project version')
    return arg_parser


def retrieve_version_number(version_file):
    pattern = re.compile(FIND_VERSION_REGEX)
    with open(version_file, 'r') as file:
        for line in file:
            search = pattern.search(line)
            if search:
                return search.group(1)
    raise RuntimeError("Couldn't find the version number in file " + version_file)


def increment_version_number(version_number):
    pattern = re.compile(VERSION_NUMBER_REGEX)
    if not pattern.match(version_number):
        raise RuntimeError('Invalid version number: ' + version_number)
    return re.sub('\\.(\\d(?!\\d))$', lambda x: '.' + str(int(x.group(1)) + 1), version_number)


def replace_version_number_in_file(version_file, old_version, new_version):
    pattern = re.compile(FIND_VERSION_REGEX)
    with fileinput.FileInput(version_file, inplace=True) as file:
        for line in file:
            if pattern.search(line):
                print(line.replace(old_version, new_version), end='')
            else:
                print(line, end='')

    detected_version = retrieve_version_number(version_file)
    assert detected_version == new_version


if __name__ == "__main__":
    main()
