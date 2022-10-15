
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.textract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.DocumentMetadata;
import software.amazon.awssdk.services.textract.model.TextractException;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DetectDocumentText {

	public static void main(String[] args) {

		final String usage = "\n" + "Usage:\n" + "    <sourceDoc> \n\n" + "Where:\n"
				+ "    sourceDoc - The path where the document is located (must be an image, for example, C:/AWS/book.png). \n";

		if (args.length != 1) {
			System.out.println(usage);
			System.exit(1);
		}

		String sourceDoc = args[0];
		Region region = Region.AP_SOUTH_1;
		TextractClient textractClient = TextractClient.builder().region(region)
				.credentialsProvider(ProfileCredentialsProvider.create()).build();

		detectDocText(textractClient, sourceDoc);
		textractClient.close();
	}

	public static void detectDocText(TextractClient textractClient, String sourceDoc) {

		try {
			InputStream sourceStream = new FileInputStream(new File(sourceDoc));
			SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

			// Get the input Document object as bytes
			Document myDoc = Document.builder().bytes(sourceBytes).build();

			DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder().document(myDoc)
					.build();

			// Invoke the Detect operation
			DetectDocumentTextResponse textResponse = textractClient.detectDocumentText(detectDocumentTextRequest);
			List<Block> docInfo = textResponse.blocks();
			for (Block block : docInfo) {
//				System.out.println("The block type is " + block.blockType().toString());
				System.out.println(block);
			}

			DocumentMetadata documentMetadata = textResponse.documentMetadata();
			System.out.println("The number of pages in the document is " + documentMetadata.pages());

		} catch (TextractException | FileNotFoundException e) {

			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
