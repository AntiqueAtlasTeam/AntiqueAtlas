package hunternif.mc.atlas;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

public class TestGit {
	public static void main(String[] args) {
		try {
			Git git = Git.open(new File("."));
			List<Ref> tags = git.tagList().call();
			Ref lastTag = tags.get(tags.size()-1);
			Ref lastTagPeeled = git.getRepository().peel(lastTag);
			ObjectId lastTagId = lastTag.getObjectId();
			Iterable<RevCommit> log = git.log().call();
			RevCommit lastCommit = log.iterator().next();
			System.out.println(ObjectId.toString(lastTagPeeled.getPeeledObjectId()));
			System.out.println(ObjectId.toString(lastCommit.getId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
