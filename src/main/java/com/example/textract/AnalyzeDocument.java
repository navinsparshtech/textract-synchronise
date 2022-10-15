package com.example.textract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.FeatureType;
import software.amazon.awssdk.services.textract.model.TextractException;

public class AnalyzeDocument {

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

		analyzeDoc(textractClient, sourceDoc);
		textractClient.close();

	}

	public static void analyzeDoc(TextractClient textractClient, String sourceDoc) {

		try {
			InputStream sourceStream = new FileInputStream(new File(sourceDoc));
			SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

			// Get the input Document object as bytes
			Document myDoc = Document.builder().bytes(sourceBytes).build();

			List<FeatureType> featureTypes = new ArrayList<FeatureType>();
			featureTypes.add(FeatureType.FORMS);
//			featureTypes.add(FeatureType.TABLES);

			AnalyzeDocumentRequest analyzeDocumentRequest = AnalyzeDocumentRequest.builder().featureTypes(featureTypes)
					.document(myDoc).build();
//			AnalyzeDocumentRequest analyzeDocumentRequest = AnalyzeDocumentRequest.builder().queriesConfig().document(myDoc).build();

			AnalyzeDocumentResponse analyzeDocument = textractClient.analyzeDocument(analyzeDocumentRequest);
			List<Block> docInfo = analyzeDocument.blocks();
			Iterator<Block> blockIterator = docInfo.iterator();

			while (blockIterator.hasNext()) {
				Block block = blockIterator.next();
//				System.out
//						.println("The block type is " + block.blockType().toString() + " : " + block.text().toString());
				System.out.println(block);
			}

		} catch (TextractException | FileNotFoundException e) {

			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

}
